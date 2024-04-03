insert into categories (id, name) values
                                      (gen_random_uuid(), 'World'),
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

insert into articles (id, title, content, category_id, created, modified) values
            (gen_random_uuid(),
            'Technology title 1',
            'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
            'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
            'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
            'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
            'remaining essentially unchanged. ' ||
            'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
            'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
            (select id from categories where name = 'Technology'),
             now(),
             now()),

            (gen_random_uuid(),
             'Science title 1',
             'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
             'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
             'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
             'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
             'remaining essentially unchanged. ' ||
             'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
             'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
             (select id from categories where name = 'Science'),
             now(),
             now()),

            (gen_random_uuid(),
             'Technology title 2',
             'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
             'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
             'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
             'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
             'remaining essentially unchanged. ' ||
             'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
             'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
             (select id from categories where name = 'Technology'),
             now(),
             now()),

            (gen_random_uuid(),
             'Design title 1',
             'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
             'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
             'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
             'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
             'remaining essentially unchanged. ' ||
             'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
             'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
             (select id from categories where name = 'Design'),
             now(),
             now()),

            (gen_random_uuid(),
             'Travel title 1',
             'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
             'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
             'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
             'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
             'remaining essentially unchanged. ' ||
             'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
             'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
             (select id from categories where name = 'Travel'),
             now(),
             now()),

            (gen_random_uuid(),
             'Technology title 3',
             'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' ||
             'Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, ' ||
             'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' ||
             'It has survived not only five centuries, but also the leap into electronic typesetting, ' ||
             'remaining essentially unchanged. ' ||
             'It was popularised in the 1960s with the release of enetreset sheets containing Lorem Ipsum passages, ' ||
             'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
             (select id from categories where name = 'Technology'),
             now(),
             now());
