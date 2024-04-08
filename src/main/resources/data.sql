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
        now(),
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
        now(),
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
        now(),
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
        now(),
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
        now(),
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
        now(),
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
        now(),
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
        now(),
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
        now(),
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
        now(),
        now());

insert into article_stats (id, likes, views, article_id)
values  (gen_random_uuid(),  5, 10, (select id from articles where image_url = 'https://picsum.photos/id/14/300/200')),
        (gen_random_uuid(), 11, 54, (select id from articles where image_url = 'https://picsum.photos/id/24/300/200')),
        (gen_random_uuid(), 21, 77, (select id from articles where image_url = 'https://picsum.photos/id/34/300/200')),
        (gen_random_uuid(), 52, 92, (select id from articles where image_url = 'https://picsum.photos/id/44/300/200')),
        (gen_random_uuid(), 33, 48, (select id from articles where image_url = 'https://picsum.photos/id/54/300/200')),
        (gen_random_uuid(), 20, 67, (select id from articles where image_url = 'https://picsum.photos/id/64/300/200')),
        (gen_random_uuid(), 19, 70, (select id from articles where image_url = 'https://picsum.photos/id/74/300/200')),
        (gen_random_uuid(), 41, 99, (select id from articles where image_url = 'https://picsum.photos/id/84/300/200')),
        (gen_random_uuid(),  3, 11, (select id from articles where image_url = 'https://picsum.photos/id/94/300/200')),
        (gen_random_uuid(), 17, 38, (select id from articles where image_url = 'https://picsum.photos/id/99/300/200'));


update articles
set title_url = CONCAT(REPLACE(LOWER(CONCAT(title, '-', RIGHT(id::text, 6))), ' ', '-'));