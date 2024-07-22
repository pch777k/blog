package com.pch777.blog.subscription.service;

import com.pch777.blog.exception.UserNotFoundException;
import com.pch777.blog.notification.domain.model.NotificationType;
import com.pch777.blog.notification.service.NotificationService;
import com.pch777.blog.subscription.domain.model.Subscription;
import com.pch777.blog.subscription.domain.repository.SubscriptionRepository;
import com.pch777.blog.identity.author.domain.model.Author;
import com.pch777.blog.identity.reader.domain.model.Reader;
import com.pch777.blog.identity.author.domain.repository.AuthorRepository;
import com.pch777.blog.identity.reader.domain.repository.ReaderRepository;
import com.pch777.blog.subscription.dto.ReaderSubscriptionDto;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ReaderRepository readerRepository;
    private final AuthorRepository authorRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public Subscription getSubscriptionById(UUID id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Subscription> getSubscriptionsByAuthorId(UUID id) {
        return subscriptionRepository.findByAuthorId(id);
    }

    @Transactional(readOnly = true)
    public List<Subscription> getSubscriptionsByReaderId(UUID id) {
        return subscriptionRepository.findByReaderId(id);
    }

    @Transactional(readOnly = true)
    public boolean hasSubscription(UUID authorId, String username) {
        Reader reader = findReaderByUsername(username);
        Author author = findAuthorById(authorId);
        return subscriptionRepository.existsSubscriptionByAuthorAndReader(author, reader);
    }

    @PreAuthorize("hasAuthority('USER_SUBSCRIBE')")
    @Transactional
    public Subscription subscribeToAuthor(UUID authorId, String username) {
        Reader reader = findReaderByUsername(username);
        Author author = findAuthorById(authorId);

        if (subscriptionRepository.existsSubscriptionByAuthorAndReader(author, reader)) {
            throw new EntityExistsException("Subscription for these author and reader exists");
        }

        Subscription subscription = new Subscription(reader, author);
        sendSubscriptionNotifications(author, reader);

        log.info("Added subscription: reader {} to author {}", reader.getUsername(), author.getUsername());
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public void unsubscribeFromAuthor(UUID authorId, String username) {
        Reader reader = findReaderByUsername(username);
        Author author = findAuthorById(authorId);

        Subscription subscription = subscriptionRepository.findByAuthorIdAndReaderId(authorId, reader.getId())
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found for author id: " + authorId + " and reader id: " + reader.getId()));

        sendUnsubscriptionNotifications(author, reader);

        log.info("Deleted subscription: reader {} from author {}", reader.getUsername(), author.getUsername());
        subscriptionRepository.delete(subscription);
    }

    private Reader findReaderByUsername(String username) {
        return readerRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Reader", username));
    }

    private Author findAuthorById(UUID authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException("Author", authorId));
    }

    private void sendSubscriptionNotifications(Author author, Reader reader) {
        notificationService.createNotification(author, reader.getFullName() + " has started subscribing to your new articles", NotificationType.SUBSCRIPTION);
        notificationService.createNotification(reader, "You have begun subscribing to this author " + author.getFullName(), NotificationType.SUBSCRIPTION);
    }

    private void sendUnsubscriptionNotifications(Author author, Reader reader) {
        notificationService.createNotification(author, "Your subscriber, " + reader.getFullName() + " has decided to unsubscribe from your articles", NotificationType.UNSUBSCRIPTION);
        notificationService.createNotification(reader, "You have successfully unsubscribed from " + author.getFullName() + "'s articles.", NotificationType.UNSUBSCRIPTION);
    }

    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public Page<Subscription> getSubscribersByAuthorId(UUID id, Pageable pageable) {
        return subscriptionRepository.findByAuthorId(id, pageable);
    }

    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public Page<Subscription> getSubscribersByAuthorId(UUID id, String search, Pageable pageable) {
        return subscriptionRepository.findSubscriptionsByAuthorId(id, search, pageable);
    }

    @PreAuthorize("hasRole('READER') or hasRole('ADMIN')")
    public Page<Subscription> getSubscriptionsByReaderId(UUID id, Pageable pageable) {
        return subscriptionRepository.findByReaderId(id, pageable);
    }

    @PreAuthorize("hasRole('READER') or hasRole('ADMIN')")
    public Page<Subscription> getSubscriptionsByReaderId(UUID id, String search, Pageable pageable) {
        return subscriptionRepository.findSubscriptionsByReaderId(id, search, pageable);
    }


    public List<ReaderSubscriptionDto> getReadersSubscribedToAuthor(UUID authorId) {
        List<Subscription> subscriptions = subscriptionRepository.findByAuthorId(authorId);
        return subscriptions.stream()
                .map(s -> new ReaderSubscriptionDto(s.getReader(), s.getCreated()))
                .toList();
    }

    public Page<ReaderSubscriptionDto> getReadersSubscribedToAuthor(UUID authorId, Pageable pageable) {
        Page<Subscription> subscriptionsPage = subscriptionRepository.findByAuthorId(authorId, pageable);
        return subscriptionsPage.map(s -> new ReaderSubscriptionDto(s.getReader(), s.getCreated()));
    }
}
