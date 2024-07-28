insert into permissions (id, permission_type)
values (gen_random_uuid(), 'ARTICLE_CREATE'),
       (gen_random_uuid(), 'ARTICLE_UPDATE'),
       (gen_random_uuid(), 'ARTICLE_DELETE'),
       (gen_random_uuid(), 'ARTICLE_LIKE'),
       (gen_random_uuid(), 'COMMENT_CREATE'),
       (gen_random_uuid(), 'COMMENT_UPDATE'),
       (gen_random_uuid(), 'COMMENT_DELETE'),
       (gen_random_uuid(), 'MESSAGE_SEND'),
       (gen_random_uuid(), 'MESSAGE_RECEIVE'),
       (gen_random_uuid(), 'USER_CREATE'),
       (gen_random_uuid(), 'USER_UPDATE'),
       (gen_random_uuid(), 'USER_DELETE'),
       (gen_random_uuid(), 'USER_SUBSCRIBE');



INSERT INTO authors (id, first_name, last_name, username, password, email, avatar_url, bio, role, created, modified,
                     is_enabled)
VALUES (gen_random_uuid(),
        'Anna',
        'Williams',
        'author',
        '$2b$10$YC5BaT2zhISVL8Syo3AAZeh/12IuBki0KUy1FdVdWYpERc3obFjU2',
        'author@mail.com',
        'https://i.postimg.cc/RVhB1W6J/avatar07.jpg',
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.',
        'AUTHOR',
        (select CURRENT_DATE - (random() * interval '365 days')),
        now(),
        true);

INSERT INTO authors (id, first_name, last_name, username, password, email, avatar_url, bio, role, created, modified,
                     is_enabled)
VALUES (gen_random_uuid(),
        'Adam',
        'Adminsky',
        'admin',
        '$2a$10$4fj1Y2yjOdp3BxU36dZam.fzLzrpWDMv1lqcUtvXh3EAB3dKrz/8a',
        'admin@mail.com',
        'https://i.postimg.cc/Hxmmb3XT/avatar03.jpg',
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.',
        'ADMIN',
        (CURRENT_DATE - (random() * interval '365 days')),
        now(),
        true);

WITH admin_user AS (SELECT id
                    FROM authors
                    WHERE username = 'admin'),
     admin_permissions AS (SELECT id
                           FROM permissions)
INSERT
INTO user_permissions (user_id, permission_id)
SELECT admin_user.id, admin_permissions.id
FROM admin_user,
     admin_permissions;

INSERT INTO authors (id, first_name, last_name, username, password, email, avatar_url, bio, role, created, modified,
                     is_enabled)
VALUES (gen_random_uuid(),
        'Jason',
        'Tatum',
        'jason',
        '$2a$10$znKsNQTAGKG0qV4HB19xg.LYaWShN.wDeY33sSEpcbhhsithpdR4.',
        'jason@mail.com',
        'https://i.postimg.cc/rm9kjWNx/avatar04.jpg',
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.',
        'AUTHOR',
        (select CURRENT_DATE - (random() * interval '365 days')),
        now(),
        true);


INSERT INTO readers (id, first_name, last_name, username, password, email, avatar_url, role, created, modified,
                     is_enabled)
VALUES (gen_random_uuid(),
        'Tom',
        'Smith',
        'reader',
        '$2b$10$YC5BaT2zhISVL8Syo3AAZeh/12IuBki0KUy1FdVdWYpERc3obFjU2',
        'reader@mail.com',
        'https://i.postimg.cc/NFfwzMdV/avatar02.jpg',
        'READER',
        (select CURRENT_DATE - (random() * interval '365 days')),
        now(),
        true);

INSERT INTO readers (id, first_name, last_name, username, password, email, avatar_url, role, created, modified,
                     is_enabled)
VALUES (gen_random_uuid(),
        'John',
        'Doe',
        'john',
        '$2a$10$nVAJmbiMhM40UNY8OlNWIeGuWpClmiWINN1vgyAZs7bU3VWZ55bTS',
        'john@mail.com',
        'https://i.postimg.cc/HnZCDgKL/avatar05.jpg',
        'READER',
        (select CURRENT_DATE - (random() * interval '365 days')),
        now(),
        true);

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

insert into articles (id, title, title_url, content, image_url, category_id, author_id, created, modified)
values (gen_random_uuid(),
        'Knowledge of human nature is the beginning and end of political education.',
        'knowledge',
        '<p>Pellentesque porta lacus sed nisi sollicitudin placerat. Sed mollis tristique elit non vulputate. Morbi accumsan eros sed augue congue lobortis. Proin consectetur dapibus vulputate. Suspendisse at orci pretium, gravida eros sit amet, consequat ante. Donec molestie vitae odio et dignissim. Fusce id nibh vitae eros iaculis facilisis ut ac ex. Aenean et augue sed velit blandit finibus sed eget nunc. Ut consectetur sagittis bibendum. Cras mollis maximus felis egestas malesuada. Maecenas dictum nulla ac nulla convallis, ac pharetra diam sagittis. Aliquam nec mi leo. Donec eu lorem posuere, sollicitudin enim vitae, convallis mauris. Donec auctor iaculis tellus sit amet lobortis. Etiam aliquet erat id finibus tristique. Fusce laoreet augue ut mollis lobortis.</p>
         <p>Sed auctor dignissim efficitur. Fusce pellentesque tristique neque, eu dignissim justo auctor ut. Aliquam aliquet est et sem dignissim tincidunt. Cras id pharetra tellus. Suspendisse et dictum sapien. Mauris euismod ut libero quis accumsan. Curabitur ac dui libero. Morbi rhoncus turpis at condimentum mattis. Nulla sed eros in elit vehicula lacinia. Integer porttitor vitae risus non mattis. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed porta lobortis urna, rutrum egestas sem rutrum in.</p>
         <p>Pellentesque imperdiet nec magna et viverra. Suspendisse nec porttitor metus. Proin ac placerat lorem. Curabitur non fermentum metus, quis pretium justo. Quisque vitae fringilla felis. Nam sed ligula eu dolor blandit tristique a nec nisi. Curabitur et augue aliquet, consectetur enim sit amet, fringilla urna. Integer consectetur rhoncus dolor ut tristique. Aliquam erat volutpat. Proin sodales lorem quis libero ultricies, sit amet aliquet mi pretium.</p>
         <p>Morbi id turpis non ante pellentesque dictum et in ante. Ut in velit nisi. Aenean eu lectus mollis, vestibulum elit blandit, facilisis odio. Maecenas vitae diam ante. Cras nec sem accumsan, pulvinar justo sit amet, ultricies mauris. Suspendisse potenti. Etiam mollis sollicitudin nisi sed ultricies. Nam facilisis ornare elit viverra eleifend. Aliquam erat volutpat. Etiam sit amet tortor rhoncus, dapibus tortor nec, vehicula erat. Proin blandit lacus et lacus suscipit, eu auctor eros mollis. In ultrices sem ut tellus efficitur, id fringilla metus pretium. Phasellus porttitor, ipsum et porta bibendum, urna nibh auctor diam, id pretium sem eros eu tellus. Praesent fermentum turpis vel sem bibendum accumsan.</p>
         <p>onec vulputate congue vehicula. Fusce scelerisque condimentum mauris eu cursus. Etiam vitae lectus sit amet sapien ultrices semper quis ut diam. Donec viverra dignissim placerat. In nec enim dui. Nunc nulla est, scelerisque eget pulvinar vel, ornare sit amet diam. Suspendisse imperdiet ornare enim, quis ornare est. Praesent eu nibh elementum nisi scelerisque ultrices. ',
        'https://picsum.photos/id/14/300/200',
        (select id from categories where name = 'Technology'),
        (select id from authors where username = 'author'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry',
        'lorem',
        '<p>Pellentesque porta lacus sed nisi sollicitudin placerat. Sed mollis tristique elit non vulputate. Morbi accumsan eros sed augue congue lobortis. Proin consectetur dapibus vulputate. Suspendisse at orci pretium, gravida eros sit amet, consequat ante. Donec molestie vitae odio et dignissim. Fusce id nibh vitae eros iaculis facilisis ut ac ex. Aenean et augue sed velit blandit finibus sed eget nunc. Ut consectetur sagittis bibendum. Cras mollis maximus felis egestas malesuada. Maecenas dictum nulla ac nulla convallis, ac pharetra diam sagittis. Aliquam nec mi leo. Donec eu lorem posuere, sollicitudin enim vitae, convallis mauris. Donec auctor iaculis tellus sit amet lobortis. Etiam aliquet erat id finibus tristique. Fusce laoreet augue ut mollis lobortis.</p>
         <p>Sed auctor dignissim efficitur. Fusce pellentesque tristique neque, eu dignissim justo auctor ut. Aliquam aliquet est et sem dignissim tincidunt. Cras id pharetra tellus. Suspendisse et dictum sapien. Mauris euismod ut libero quis accumsan. Curabitur ac dui libero. Morbi rhoncus turpis at condimentum mattis. Nulla sed eros in elit vehicula lacinia. Integer porttitor vitae risus non mattis. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed porta lobortis urna, rutrum egestas sem rutrum in.</p>
         <p>Pellentesque imperdiet nec magna et viverra. Suspendisse nec porttitor metus. Proin ac placerat lorem. Curabitur non fermentum metus, quis pretium justo. Quisque vitae fringilla felis. Nam sed ligula eu dolor blandit tristique a nec nisi. Curabitur et augue aliquet, consectetur enim sit amet, fringilla urna. Integer consectetur rhoncus dolor ut tristique. Aliquam erat volutpat. Proin sodales lorem quis libero ultricies, sit amet aliquet mi pretium.</p>
         <p>Morbi id turpis non ante pellentesque dictum et in ante. Ut in velit nisi. Aenean eu lectus mollis, vestibulum elit blandit, facilisis odio. Maecenas vitae diam ante. Cras nec sem accumsan, pulvinar justo sit amet, ultricies mauris. Suspendisse potenti. Etiam mollis sollicitudin nisi sed ultricies. Nam facilisis ornare elit viverra eleifend. Aliquam erat volutpat. Etiam sit amet tortor rhoncus, dapibus tortor nec, vehicula erat. Proin blandit lacus et lacus suscipit, eu auctor eros mollis. In ultrices sem ut tellus efficitur, id fringilla metus pretium. Phasellus porttitor, ipsum et porta bibendum, urna nibh auctor diam, id pretium sem eros eu tellus. Praesent fermentum turpis vel sem bibendum accumsan.</p>
         <p>onec vulputate congue vehicula. Fusce scelerisque condimentum mauris eu cursus. Etiam vitae lectus sit amet sapien ultrices semper quis ut diam. Donec viverra dignissim placerat. In nec enim dui. Nunc nulla est, scelerisque eget pulvinar vel, ornare sit amet diam. Suspendisse imperdiet ornare enim, quis ornare est. Praesent eu nibh elementum nisi scelerisque ultrices. ',
        'https://picsum.photos/id/24/300/200',
        (select id from categories where name = 'Travel'),
        (select id from authors where username = 'jason'),
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
        (select id from categories where name = 'Design'),
        (select id from authors where username = 'author'),
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
        (select id from categories where name = 'Travel'),
        (select id from authors where username = 'jason'),
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
        (select id from categories where name = 'Travel'),
        (select id from authors where username = 'author'),
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
        (select id from categories where name = 'Technology'),
        (select id from authors where username = 'jason'),
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
        (select id from categories where name = 'Travel'),
        (select id from authors where username = 'author'),
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
        (select id from categories where name = 'Design'),
        (select id from authors where username = 'jason'),
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
        (select id from categories where name = 'Travel'),
        (select id from authors where username = 'author'),
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
        (select id from categories where name = 'Technology'),
        (select id from authors where username = 'jason'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'The Wonders of the World',
        'title_url',
        '<p>Pellentesque porta lacus sed nisi sollicitudin placerat. Sed mollis tristique elit non vulputate. Morbi accumsan eros sed augue congue lobortis. Proin consectetur dapibus vulputate. Suspendisse at orci pretium, gravida eros sit amet, consequat ante. Donec molestie vitae odio et dignissim. Fusce id nibh vitae eros iaculis facilisis ut ac ex. Aenean et augue sed velit blandit finibus sed eget nunc. Ut consectetur sagittis bibendum. Cras mollis maximus felis egestas malesuada. Maecenas dictum nulla ac nulla convallis, ac pharetra diam sagittis. Aliquam nec mi leo. Donec eu lorem posuere, sollicitudin enim vitae, convallis mauris. Donec auctor iaculis tellus sit amet lobortis. Etiam aliquet erat id finibus tristique. Fusce laoreet augue ut mollis lobortis.</p>
         <p>Sed auctor dignissim efficitur. Fusce pellentesque tristique neque, eu dignissim justo auctor ut. Aliquam aliquet est et sem dignissim tincidunt. Cras id pharetra tellus. Suspendisse et dictum sapien. Mauris euismod ut libero quis accumsan. Curabitur ac dui libero. Morbi rhoncus turpis at condimentum mattis. Nulla sed eros in elit vehicula lacinia. Integer porttitor vitae risus non mattis. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed porta lobortis urna, rutrum egestas sem rutrum in.</p>
         <p>Pellentesque imperdiet nec magna et viverra. Suspendisse nec porttitor metus. Proin ac placerat lorem. Curabitur non fermentum metus, quis pretium justo. Quisque vitae fringilla felis. Nam sed ligula eu dolor blandit tristique a nec nisi. Curabitur et augue aliquet, consectetur enim sit amet, fringilla urna. Integer consectetur rhoncus dolor ut tristique. Aliquam erat volutpat. Proin sodales lorem quis libero ultricies, sit amet aliquet mi pretium.</p>
         <p>Morbi id turpis non ante pellentesque dictum et in ante. Ut in velit nisi. Aenean eu lectus mollis, vestibulum elit blandit, facilisis odio. Maecenas vitae diam ante. Cras nec sem accumsan, pulvinar justo sit amet, ultricies mauris. Suspendisse potenti. Etiam mollis sollicitudin nisi sed ultricies. Nam facilisis ornare elit viverra eleifend. Aliquam erat volutpat. Etiam sit amet tortor rhoncus, dapibus tortor nec, vehicula erat. Proin blandit lacus et lacus suscipit, eu auctor eros mollis. In ultrices sem ut tellus efficitur, id fringilla metus pretium. Phasellus porttitor, ipsum et porta bibendum, urna nibh auctor diam, id pretium sem eros eu tellus. Praesent fermentum turpis vel sem bibendum accumsan.</p>
         <p>onec vulputate congue vehicula. Fusce scelerisque condimentum mauris eu cursus. Etiam vitae lectus sit amet sapien ultrices semper quis ut diam. Donec viverra dignissim placerat. In nec enim dui. Nunc nulla est, scelerisque eget pulvinar vel, ornare sit amet diam. Suspendisse imperdiet ornare enim, quis ornare est. Praesent eu nibh elementum nisi scelerisque ultrices. ',
        'https://picsum.photos/id/1015/300/200',
        (select id from categories where name = 'World'),
        (select id from authors where username = 'author'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Life in the United States',
        'title_url',
        '<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla sed magna rutrum, gravida dolor a, finibus risus. Sed tincidunt risus et tincidunt elementum. Morbi mauris urna, finibus sed leo eu, volutpat semper nisi. Suspendisse potenti. Maecenas at est scelerisque, venenatis sem at, vehicula dolor. Pellentesque at tortor imperdiet purus tincidunt rhoncus. Proin varius elementum condimentum. Phasellus finibus tellus tristique ligula fermentum ultrices. Suspendisse a sodales est. Donec vitae purus felis. Sed ultrices, augue et tristique lobortis, neque leo aliquet nulla, sed maximus sapien purus sit amet libero. Fusce a ligula sit amet velit congue maximus et eu magna. Maecenas egestas, turpis non condimentum placerat, elit odio pulvinar magna, nec pharetra justo ante eget dolor. Integer sed nisi ac mi pulvinar efficitur sit amet venenatis magna. Nam laoreet luctus mauris, sit amet aliquam lorem aliquam eget.</p>
         <p>Fusce lectus dui, varius et lorem eu, rhoncus faucibus nisi. Phasellus mollis justo a risus bibendum, in efficitur ante volutpat. Phasellus mattis maximus scelerisque. Etiam sed nisi sed nunc suscipit efficitur eget ac sem. Sed molestie leo turpis, quis imperdiet orci mollis ac. Vivamus non urna lectus. Fusce vestibulum purus a lorem mattis, nec hendrerit risus porta. Pellentesque ut gravida augue. Suspendisse sit amet eros efficitur, finibus justo ac, interdum diam. Cras in quam tellus. Ut ut sodales nisl. Quisque molestie leo vitae bibendum feugiat. Etiam viverra venenatis nibh vitae egestas.</p>
         <p>Nunc feugiat arcu condimentum malesuada sodales. Praesent convallis justo vulputate, ornare nulla non, feugiat nisl. Nulla arcu dolor, facilisis vitae ullamcorper vel, pulvinar egestas nulla. Mauris viverra congue mi, ut lacinia lectus venenatis sed. Suspendisse potenti. In sodales vulputate tortor eu lacinia. Fusce rutrum eget velit sed dictum. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Nulla volutpat aliquam felis, quis volutpat elit volutpat eget. Praesent lectus elit, egestas sed pulvinar et, luctus sed est. Donec fermentum dolor id elit consectetur elementum.</p>
         <p>Integer est diam, tempus ac fermentum malesuada, malesuada id eros. Duis ac mauris sed libero aliquet congue non a dolor. In sollicitudin volutpat accumsan. Donec pellentesque non massa id dictum. Maecenas ac consectetur arcu. Fusce velit est, vestibulum ut eros at, tempus condimentum eros. Mauris cursus metus vitae tellus suscipit ultrices. Aenean ac tellus at tortor suscipit scelerisque. Cras turpis mauris, molestie nec porttitor vitae, placerat ac massa. Integer vitae lorem in eros rutrum mollis. Pellentesque a ligula lorem. Etiam venenatis nec nulla sed venenatis. Nullam faucibus lorem id laoreet aliquet. Nunc auctor leo vitae risus bibendum, nec pharetra nisl accumsan.</p>
         <p>Phasellus ex purus, ultrices quis facilisis at, bibendum at neque. Vivamus molestie lectus eget tempor dignissim. Suspendisse cursus nulla et dui vehicula consectetur. Sed porttitor quis dolor et suscipit. Aenean lectus ex, porta sed tincidunt vitae, aliquam sit amet sem. Aliquam pellentesque lacus vel lacus pretium vestibulum. Integer scelerisque lorem sed est tristique rutrum. Vivamus non ullamcorper justo.</p>
         <p>Integer laoreet placerat magna nec euismod. Cras venenatis ex eu metus tempor mollis. Ut commodo, nibh nec porttitor ultrices, sapien elit aliquam augue, nec mattis lorem mauris a ipsum. Nulla aliquam augue ac augue condimentum suscipit. Phasellus commodo libero ut mauris pellentesque, at mollis eros malesuada. In tincidunt leo et nisi semper venenatis. Etiam a purus sem. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Curabitur pharetra eget nibh at molestie. Nulla elementum ex libero, porta hendrerit risus imperdiet ut. Donec elementum dui commodo, tempus libero et, varius lacus. Nullam vitae lorem magna.</p>',
        'https://picsum.photos/id/1020/300/200',
        (select id from categories where name = 'U.S.'),
        (select id from authors where username = 'jason'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'The Future of Technology',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, ' ||
        'totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt ' ||
        'explicabo.',
        'https://picsum.photos/id/925/300/200',
        (select id from categories where name = 'Technology'),
        (select id from authors where username = 'author'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'The Art of Design',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/930/300/200',
        (select id from categories where name = 'Design'),
        (select id from authors where username = 'jason'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Exploring Different Cultures',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1035/300/200',
        (select id from categories where name = 'Culture'),
        (select id from authors where username = 'author'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Business Strategies for Success',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1040/300/200',
        (select id from categories where name = 'Business'),
        (select id from authors where username = 'jason'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'The World of Politics',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1045/300/200',
        (select id from categories where name = 'Politics'),
        (select id from authors where username = 'author'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Personal Opinions and Perspectives',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1050/300/200',
        (select id from categories where name = 'Opinion'),
        (select id from authors where username = 'jason'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'The Wonders of Science',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1055/300/200',
        (select id from categories where name = 'Science'),
        (select id from authors where username = 'author'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Health and Wellness Tips',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1060/300/200',
        (select id from categories where name = 'Health'),
        (select id from authors where username = 'author'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Fashion and Style Trends',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1065/300/200',
        (select id from categories where name = 'Style'),
        (select id from authors where username = 'author'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now()),

       (gen_random_uuid(),
        'Travel Destinations Around the World',
        'title_url',
        'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
        'https://picsum.photos/id/1070/300/200',
        (select id from categories where name = 'Travel'),
        (select id from authors where username = 'jason'),
        (select CURRENT_DATE - (random() * interval '365 days')),
        now());

insert into article_stats (id, likes, views, time_to_read, article_id)
values (gen_random_uuid(), 5, 10, 1, (select id from articles where image_url = 'https://picsum.photos/id/14/300/200')),
       (gen_random_uuid(), 11, 54, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/24/300/200')),
       (gen_random_uuid(), 21, 77, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/34/300/200')),
       (gen_random_uuid(), 52, 92, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/44/300/200')),
       (gen_random_uuid(), 33, 48, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/54/300/200')),
       (gen_random_uuid(), 20, 67, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/64/300/200')),
       (gen_random_uuid(), 19, 70, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/74/300/200')),
       (gen_random_uuid(), 41, 99, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/84/300/200')),
       (gen_random_uuid(), 3, 11, 1, (select id from articles where image_url = 'https://picsum.photos/id/94/300/200')),
       (gen_random_uuid(), 17, 38, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/99/300/200')),
       (gen_random_uuid(), 22, 50, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/1015/300/200')),
       (gen_random_uuid(), 9, 36, 4,
        (select id from articles where image_url = 'https://picsum.photos/id/1020/300/200')),
       (gen_random_uuid(), 15, 47, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/925/300/200')),
       (gen_random_uuid(), 6, 15, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/930/300/200')),
       (gen_random_uuid(), 1, 20, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/1035/300/200')),
       (gen_random_uuid(), 32, 68, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/1040/300/200')),
       (gen_random_uuid(), 27, 59, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/1045/300/200')),
       (gen_random_uuid(), 8, 81, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/1050/300/200')),
       (gen_random_uuid(), 33, 72, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/1055/300/200')),
       (gen_random_uuid(), 14, 39, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/1060/300/200')),
       (gen_random_uuid(), 25, 45, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/1065/300/200')),
       (gen_random_uuid(), 35, 68, 1,
        (select id from articles where image_url = 'https://picsum.photos/id/1070/300/200'));

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

insert into comments (id, article_id, user_id, content, created, modified)
values (gen_random_uuid(),
        (select id from articles where image_url = 'https://picsum.photos/id/14/300/200'),
        (select id from authors where username = 'author'),
        'Aliquam erat volutpat. Etiam sit amet tortor rhoncus, dapibus tortor nec, vehicula erat. Proin blandit lacus et lacus suscipit, eu auctor eros mollis. In ultrices sem ut tellus efficitur, id fringilla metus pretium.', now(), now());


update articles
set title_url = CONCAT(REPLACE(LOWER(CONCAT(title, '-', RIGHT(id::text, 6))), ' ', '-'));

INSERT INTO comments (id, article_id, user_id, content, created, modified)
SELECT gen_random_uuid(),
       a.id,
       (select id from authors where username = 'author'),
       'Aliquam erat volutpat. Etiam sit amet tortor rhoncus, dapibus tortor nec, vehicula erat. Proin blandit lacus et lacus suscipit, eu auctor eros mollis. In ultrices sem ut tellus efficitur, id fringilla metus pretium.',
       LEAST(a.created + INTERVAL '1 day' + (random() * INTERVAL '10 days') + (random() * INTERVAL '86400 seconds'),
             NOW()),
       NOW()
FROM articles a
         CROSS JOIN LATERAL generate_series(1, 10) s;

INSERT INTO comments (id, article_id, user_id, content, created, modified)
SELECT gen_random_uuid(),
       a.id,
       (select id from readers where username = 'reader'),
       'Morbi id turpis non ante pellentesque dictum et in ante. Ut in velit nisi. Aenean eu lectus mollis, vestibulum elit blandit, facilisis odio. Maecenas vitae diam ante.',
       LEAST(a.created + INTERVAL '1 day' + (random() * INTERVAL '10 days') + (random() * INTERVAL '86400 seconds'),
             NOW()),
       NOW()
FROM articles a
         CROSS JOIN LATERAL generate_series(1, 3) s;

INSERT INTO comments (id, article_id, user_id, content, created, modified)
SELECT gen_random_uuid(),
       a.id,
       (select id from readers where username = 'john'),
       ' Cras nec sem accumsan, pulvinar justo sit amet, ultricies mauris. Suspendisse potenti. Etiam mollis sollicitudin nisi sed ultricies. Nam facilisis ornare elit viverra eleifend',
       LEAST(a.created + INTERVAL '1 day' + (random() * INTERVAL '10 days') + (random() * INTERVAL '86400 seconds'),
             NOW()),
       NOW()
FROM articles a
         CROSS JOIN LATERAL generate_series(1, 3) s;
