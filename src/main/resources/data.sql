insert into categories (id, name)
values (gen_random_uuid(), 'World'),
       (gen_random_uuid(), 'U.S.'),
       (gen_random_uuid(), 'Technology'),
       (gen_random_uuid(), 'Design'),
       (gen_random_uuid(), 'Culture'),
       (gen_random_uuid(), 'Business'),
       (gen_random_uuid(), 'Politics'),
       (gen_random_uuid(), 'Opinion'),
       (gen_random_uuid(), 'Science'),
       (gen_random_uuid(), 'Health'),
       (gen_random_uuid(), 'Style'),
       (gen_random_uuid(), 'Travel');

insert into articles (id, title, title_url, content, image_url, time_to_read, category_id, created, modified)
values (gen_random_uuid(),
        'Knowledge of human nature is the beginning and end of political education.',
        'knowledge',
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
        'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
        'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
        'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
        'remaining essentially unchanged. ' ||
        'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
        'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
        'https://picsum.photos/id/14/300/200',
        2,
        (select id from categories where name = 'Technology'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry',
        'lorem',
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
        'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
        'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
        'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
        'remaining essentially unchanged. ' ||
        'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
        'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
        'https://picsum.photos/id/24/300/200',
        3,
        (select id from categories where name = 'Travel'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Lorem Ipsum - An Introduction to Lorem Ipsum',
        'lorem',
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
        'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
        'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
        'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
        'remaining essentially unchanged. ' ||
        'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
        'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
        'https://picsum.photos/id/34/300/200',
        3,
        (select id from categories where name = 'Design'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'The History of Lorem Ipsum',
        'lorem',
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
        'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
        'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
        'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
        'remaining essentially unchanged. ' ||
        'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
        'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
        'https://picsum.photos/id/44/300/200',
        3,
        (select id from categories where name = 'Travel'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'It has survived not only five centuries',
        'lorem',
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
        'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
        'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
        'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
        'remaining essentially unchanged. ' ||
        'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
        'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
        'https://picsum.photos/id/54/300/200',
        3,
        (select id from categories where name = 'Travel'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry',
        'lorem',
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
        'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
        'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
        'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
        'remaining essentially unchanged. ' ||
        'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
        'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
        'https://picsum.photos/id/64/300/200',
        3,
        (select id from categories where name = 'Technology'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry',
        'lorem',
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
        'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
        'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
        'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
        'remaining essentially unchanged. ' ||
        'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
        'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
        'https://picsum.photos/id/74/300/200',
        3,
        (select id from categories where name = 'Travel'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry',
        'lorem',
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
        'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
        'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
        'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
        'remaining essentially unchanged. ' ||
        'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
        'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
        'https://picsum.photos/id/84/300/200',
        3,
        (select id from categories where name = 'Design'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry',
        'lorem',
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
        'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
        'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
        'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
        'remaining essentially unchanged. ' ||
        'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
        'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
        'https://picsum.photos/id/94/300/200',
        3,
        (select id from categories where name = 'Travel'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'It has survived not only five centuries, but also the leap into electronic typesetting',
        'It has survived not only five centuries, but also the leap into electronic typesetting',
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
        'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
        'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
        'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
        'remaining essentially unchanged. ' ||
        'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
        'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
        'https://picsum.photos/id/99/300/200',
        2,
        (select id from categories where name = 'Technology'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'The Wonders of the World',
        'title_url',
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed ut perspiciatis unde omnis iste natus error sit ' ||
        'voluptatem accusantium doloremque laudantium.',
        'https://picsum.photos/id/1015/300/200',
        3,
        (select id from categories where name = 'World'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Life in the United States',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam.',
        'https://picsum.photos/id/1020/300/200',
        4,
        (select id from categories where name = 'U.S.'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'The Future of Technology',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/925/300/200',
        5,
        (select id from categories where name = 'Technology'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'The Art of Design',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/930/300/200',
        6,
        (select id from categories where name = 'Design'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Exploring Different Cultures',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1035/300/200',
        7,
        (select id from categories where name = 'Culture'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Business Strategies for Success',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1040/300/200',
        8,
        (select id from categories where name = 'Business'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'The World of Politics',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1045/300/200',
        9,
        (select id from categories where name = 'Politics'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Personal Opinions and Perspectives',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1050/300/200',
        10,
        (select id from categories where name = 'Opinion'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'The Wonders of Science',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1055/300/200',
        11,
        (select id from categories where name = 'Science'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Health and Wellness Tips',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1060/300/200',
        12,
        (select id from categories where name = 'Health'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Fashion and Style Trends',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1065/300/200',
        13,
        (select id from categories where name = 'Style'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Travel Destinations Around the World',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1070/300/200',
        14,
        (select id from categories where name = 'Travel'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now());

insert into article_stats (id, likes, views, article_id)
values (gen_random_uuid(), 5, 10, (select id from articles where image_url = 'https://picsum.photos/id/14/300/200')),
       (gen_random_uuid(), 11, 54, (select id from articles where image_url = 'https://picsum.photos/id/24/300/200')),
       (gen_random_uuid(), 21, 77, (select id from articles where image_url = 'https://picsum.photos/id/34/300/200')),
       (gen_random_uuid(), 52, 92, (select id from articles where image_url = 'https://picsum.photos/id/44/300/200')),
       (gen_random_uuid(), 33, 48, (select id from articles where image_url = 'https://picsum.photos/id/54/300/200')),
       (gen_random_uuid(), 20, 67, (select id from articles where image_url = 'https://picsum.photos/id/64/300/200')),
       (gen_random_uuid(), 19, 70, (select id from articles where image_url = 'https://picsum.photos/id/74/300/200')),
       (gen_random_uuid(), 41, 99, (select id from articles where image_url = 'https://picsum.photos/id/84/300/200')),
       (gen_random_uuid(), 3, 11, (select id from articles where image_url = 'https://picsum.photos/id/94/300/200')),
       (gen_random_uuid(), 17, 38, (select id from articles where image_url = 'https://picsum.photos/id/99/300/200')),
       (gen_random_uuid(), 22, 50, (select id from articles where image_url = 'https://picsum.photos/id/1015/300/200')),
       (gen_random_uuid(), 9, 36, (select id from articles where image_url = 'https://picsum.photos/id/1020/300/200')),
       (gen_random_uuid(), 15, 47, (select id from articles where image_url = 'https://picsum.photos/id/925/300/200')),
       (gen_random_uuid(), 6, 15, (select id from articles where image_url = 'https://picsum.photos/id/930/300/200')),
       (gen_random_uuid(), 1, 20, (select id from articles where image_url = 'https://picsum.photos/id/1035/300/200')),
       (gen_random_uuid(), 32, 68, (select id from articles where image_url = 'https://picsum.photos/id/1040/300/200')),
       (gen_random_uuid(), 27, 59, (select id from articles where image_url = 'https://picsum.photos/id/1045/300/200')),
       (gen_random_uuid(), 8, 81, (select id from articles where image_url = 'https://picsum.photos/id/1050/300/200')),
       (gen_random_uuid(), 33, 72, (select id from articles where image_url = 'https://picsum.photos/id/1055/300/200')),
       (gen_random_uuid(), 14, 39, (select id from articles where image_url = 'https://picsum.photos/id/1060/300/200')),
       (gen_random_uuid(), 25, 45, (select id from articles where image_url = 'https://picsum.photos/id/1065/300/200')),
       (gen_random_uuid(), 35, 68, (select id from articles where image_url = 'https://picsum.photos/id/1070/300/200'));

insert into tags (id, name)
values (gen_random_uuid(), 'asia'),
       (gen_random_uuid(), 'adventure'),
       (gen_random_uuid(), 'music'),
       (gen_random_uuid(), 'film'),
       (gen_random_uuid(), 'literature'),
       (gen_random_uuid(), 'finance'),
       (gen_random_uuid(), 'economy'),
       (gen_random_uuid(), 'government'),
       (gen_random_uuid(), 'democracy'),
       (gen_random_uuid(), 'fitness'),
       (gen_random_uuid(), 'tourism'),
       (gen_random_uuid(), 'destination');

insert into tags_articles (articles_id, tags_id)
values ((select id from articles where image_url = 'https://picsum.photos/id/14/300/200'),
        (select id from tags where name = 'asia')),
       ((select id from articles where image_url = 'https://picsum.photos/id/14/300/200'),
        (select id from tags where name = 'adventure')),
       ((select id from articles where image_url = 'https://picsum.photos/id/24/300/200'),
        (select id from tags where name = 'adventure')),
       ((select id from articles where image_url = 'https://picsum.photos/id/24/300/200'),
        (select id from tags where name = 'destination')),
       ((select id from articles where image_url = 'https://picsum.photos/id/24/300/200'),
        (select id from tags where name = 'tourism')),
       ((select id from articles where image_url = 'https://picsum.photos/id/34/300/200'),
        (select id from tags where name = 'fitness')),
       ((select id from articles where image_url = 'https://picsum.photos/id/44/300/200'),
        (select id from tags where name = 'democracy')),
       ((select id from articles where image_url = 'https://picsum.photos/id/54/300/200'),
        (select id from tags where name = 'economy')),
       ((select id from articles where image_url = 'https://picsum.photos/id/64/300/200'),
        (select id from tags where name = 'government')),
       ((select id from articles where image_url = 'https://picsum.photos/id/74/300/200'),
        (select id from tags where name = 'literature')),
       ((select id from articles where image_url = 'https://picsum.photos/id/84/300/200'),
        (select id from tags where name = 'music')),
       ((select id from articles where image_url = 'https://picsum.photos/id/94/300/200'),
        (select id from tags where name = 'film')),
       ((select id from articles where image_url = 'https://picsum.photos/id/94/300/200'),
        (select id from tags where name = 'adventure')),
       ((select id from articles where image_url = 'https://picsum.photos/id/99/300/200'),
        (select id from tags where name = 'music'));

insert into comments (id, article_id, content, created, modified)
values (gen_random_uuid(),
        (select id from articles where image_url = 'https://picsum.photos/id/14/300/200'),
        'comment', now(), now());


update articles
set title_url = CONCAT(REPLACE(LOWER(CONCAT(title, '-', RIGHT(id::text, 6))), ' ', '-'));

INSERT INTO comments (id, article_id, content, created, modified)
SELECT
    gen_random_uuid(),
    a.id,
    'Random comment for article ' || a.title_url,
    NOW(),
    NOW()
FROM
    articles a
        CROSS JOIN LATERAL generate_series(1, 10) s

