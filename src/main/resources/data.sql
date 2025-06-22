INSERT INTO "user" (id, name, email, profile_id, image, role, phone_number, is_active, created_at, last_password_change) VALUES
                                                                                                                             ('550e8400-e29b-41d4-a716-446655440000', 'Alice Johnson', 'alice.johnson@example.com', 'alice123', 'https://example.com/images/alice.jpg', 0, '9876543210', TRUE, CURRENT_TIMESTAMP, NULL),
                                                                                                                             ('550e8400-e29b-41d4-a716-446655440001', 'Bob Smith', 'bob.smith@example.com', 'bobsmith', 'https://example.com/images/bob.jpg', 2, '9123456789', TRUE, CURRENT_TIMESTAMP, NULL),
                                                                                                                             ('550e8400-e29b-41d4-a716-446655440002', 'Carol Martinez', 'carol.martinez@example.com', 'carolmtz', NULL, 3, '9988776655', FALSE, CURRENT_TIMESTAMP, NULL),
                                                                                                                             ('550e8400-e29b-41d4-a716-446655440003', 'David Lee', 'david.lee@example.com', 'davidl', NULL, 1, '9876512340', TRUE, CURRENT_TIMESTAMP, NULL),
                                                                                                                             ('550e8400-e29b-41d4-a716-446655440004', 'Eva Green', 'eva.green@example.com', 'evagreen', NULL, 0, '9998887776', TRUE, CURRENT_TIMESTAMP, NULL),
                                                                                                                             ('550e8400-e29b-41d4-a716-446655440005', 'Frank Wright', 'frank.wright@example.com', 'frankwright', NULL, 2, '8887776665', TRUE, CURRENT_TIMESTAMP, NULL),
                                                                                                                             ('550e8400-e29b-41d4-a716-446655440006', 'Grace Kim', 'grace.kim@example.com', 'gracek', 'https://example.com/images/grace.jpg', 3, '7776665554', TRUE, CURRENT_TIMESTAMP, NULL),
                                                                                                                             ('550e8400-e29b-41d4-a716-446655440007', 'Henry Zhao', 'henry.zhao@example.com', 'henryz', NULL, 1, '6665554443', TRUE, CURRENT_TIMESTAMP, NULL),
                                                                                                                             ('550e8400-e29b-41d4-a716-446655440008', 'Isla Moore', 'isla.moore@example.com', 'islababy', NULL, 0, '5554443332', TRUE, CURRENT_TIMESTAMP, NULL),
                                                                                                                             ('550e8400-e29b-41d4-a716-446655440009', 'Jake Brown', 'jake.brown@example.com', 'jakeb', 'https://example.com/images/jake.jpg', 2, '4443332221', FALSE, CURRENT_TIMESTAMP, NULL),
                                                                                                                             ('064ed34e-473b-4b0e-b2f3-d2c2853e3de1','admin','admin@gmail.com','admin','https://example.com/images/jake.jpg',2,'4661465465',FALSE,CURRENT_TIMESTAMP,NULL),
                                                                                                                             ('54098e2d-3171-45d6-9795-cb8394ea4315','staff','staff@gmail.com','staff','https://example.com/images/jake.jpg',2,'4661465465',FALSE,CURRENT_TIMESTAMP,NULL),
                                                                                                                             ('f107a124-38a7-482f-972f-8810897e8d09','student','student@gmail.com','student','https://example.com/images/jake.jpg',2,'4661465465',FALSE,CURRENT_TIMESTAMP,NULL);


INSERT INTO semester(id,name,year,is_active) VALUES('8d0d1345-2ed6-4cd1-b020-f9c9ae71a3eb','AIE 2024',2024,true);
INSERT INTO semester(id,name,year,is_active) VALUES('81c3f9a5-3322-4f12-a735-b8c7e637d450','AIDS 2024',2024,true);
-- INSERT INTO semester(id,name,year,is_active) VALUES('6f7e1b15-753e-4c79-94d4-51e515a64763','MECH 2024',2024,true);
INSERT INTO semester(id,name,year,is_active) VALUES('51c005b7-0f0e-42ed-a6f5-9e24c648a90f','CSE 2024',2024,true);
INSERT INTO semester(id,name,year,is_active) VALUES('01359e2e-0987-4f63-b95b-e9b2b6f6258d','CIVIL 2024',2024,true);
-- 1. AI Fundamentals - Semester 1
-- 1. AI Fundamentals - Semester 1
INSERT INTO bank (
    id,
    name,
    course_code,
    semester,
    created_at,
    created_by_id
) VALUES (
             gen_random_uuid(),
             'AI Fundamentals',
             'AI101',
             1,
             NOW(),
             '550e8400-e29b-41d4-a716-446655440001'
         );

-- 2. Machine Learning Basics - Semester 2
INSERT INTO bank (
    id,
    name,
    course_code,
    semester,
    created_at,
    created_by_id
) VALUES (
             '42bb8622-58c5-4ec3-a1b1-85d10a1281d8',
             'Machine Learning Basics',
             'ML201',
             2,
             NOW(),
             '550e8400-e29b-41d4-a716-446655440001'
         );

-- 3. Data Structures - Semester 3
INSERT INTO bank (
    id,
    name,
    course_code,
    semester,
    created_at,
    created_by_id
) VALUES (
             'a194b8f0-1aa0-4be6-8003-d80f9cb00517',
             'Data Structures',
             'CS301',
             3,
             NOW(),
             '550e8400-e29b-41d4-a716-446655440001'
         );

-- 4. Database Systems - Semester 4
INSERT INTO bank (
    id,
    name,
    course_code,
    semester,
    created_at,
    created_by_id
) VALUES (
             '66cd0d4c-ecc3-4a97-8713-5d8013c1b08a',
             'Database Systems',
             'DB401',
             4,
             NOW(),
             '550e8400-e29b-41d4-a716-446655440001'
         );

-- 5. Operating Systems - Semester 5
INSERT INTO bank (
    id,
    name,
    course_code,
    semester,
    created_at,
    created_by_id
) VALUES (
             '1a432f82-cb59-4ce6-bd61-9c13a7581c12',
             'Operating Systems',
             'OS501',
             5,
             NOW(),
             '550e8400-e29b-41d4-a716-446655440001'
         );



INSERT INTO quiz (
    id,
    name,
    description,
    instructions,
    start_time,
    end_time,
    duration, -- as bigint (milliseconds)
    password,
    full_screen,
    shuffle_questions,
    shuffle_options,
    linear_quiz,
    calculator,
    auto_submit,
    publish_result,
    publish_quiz,
    created_at,
    created_by_id
)
VALUES
-- Quiz 1
('1a111111-1111-1111-1111-111111111111', 'Math Quiz 1', 'Algebra basics', 'Solve all questions',
 '2025-06-04T10:00:00Z', '2025-06-04T11:00:00Z', 3600000, 'math123',
 TRUE, TRUE, FALSE, FALSE, TRUE, TRUE, TRUE, FALSE,
 '2025-06-01T10:00:00Z', '550e8400-e29b-41d4-a716-446655440002'),

-- Quiz 2
('2a222222-2222-2222-2222-222222222222', 'Physics Quiz', 'Mechanics topics', 'Use diagram where necessary',
 '2025-06-05T09:00:00Z', '2025-06-05T10:30:00Z', 5400000, NULL,
 FALSE, TRUE, TRUE, FALSE, FALSE, TRUE, TRUE, TRUE,
 '2025-06-01T11:00:00Z', '550e8400-e29b-41d4-a716-446655440002'),

-- Quiz 3
('3a333333-3333-3333-3333-333333333333', 'Chemistry Quiz', 'Organic chemistry', 'MCQs only',
 '2025-06-06T08:30:00Z', '2025-06-06T09:30:00Z', 3600000, NULL,
 TRUE, FALSE, FALSE, TRUE, FALSE, FALSE, FALSE, TRUE,
 '2025-06-02T09:00:00Z', '550e8400-e29b-41d4-a716-446655440002'),

-- Quiz 4
('4a444444-4444-4444-4444-444444444444', 'CS Quiz 1', 'Data Structures', 'Attempt all questions',
 '2025-06-07T14:00:00Z', '2025-06-07T15:30:00Z', 5400000, 'csquiz',
 TRUE, TRUE, TRUE, FALSE, TRUE, FALSE, TRUE, TRUE,
 '2025-06-03T10:00:00Z', '550e8400-e29b-41d4-a716-446655440007'),

-- Quiz 5
('5a555555-5555-5555-5555-555555555555', 'General Quiz', 'Mix of all subjects', 'Best of luck',
 '2025-06-08T13:00:00Z', '2025-06-08T14:00:00Z', 3600000, NULL,
 FALSE, FALSE, FALSE, FALSE, FALSE, TRUE, FALSE, FALSE,
 '2025-06-04T12:00:00Z', '550e8400-e29b-41d4-a716-446655440007'),

-- Quiz 6
('6a666666-6666-6666-6666-666666666666', 'History Quiz', 'World history', 'Answer precisely',
 '2025-06-09T10:00:00Z', '2025-06-09T11:00:00Z', 3600000, NULL,
 FALSE, TRUE, TRUE, FALSE, FALSE, FALSE, TRUE, FALSE,
 '2025-06-05T09:00:00Z', '550e8400-e29b-41d4-a716-446655440007'),

-- Quiz 7
('7a777777-7777-7777-7777-777777777777', 'Geography Quiz', 'Earth & Continents', NULL,
 '2025-06-10T10:00:00Z', '2025-06-10T11:00:00Z', 3600000, NULL,
 TRUE, FALSE, TRUE, FALSE, TRUE, TRUE, FALSE, FALSE,
 '2025-06-05T10:00:00Z', '550e8400-e29b-41d4-a716-446655440009'),

-- Quiz 8
('8a888888-8888-8888-8888-888888888888', 'Biology Quiz', 'Human Anatomy', NULL,
 '2025-06-11T10:00:00Z', '2025-06-11T11:00:00Z', 3600000, NULL,
 TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE,
 '2025-06-05T11:00:00Z', '550e8400-e29b-41d4-a716-446655440009'),

-- Quiz 9
('9a999999-9999-9999-9999-999999999999', 'Economics Quiz', 'Microeconomics', NULL,
 '2025-06-12T10:00:00Z', '2025-06-12T11:00:00Z', 3600000, NULL,
 FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE,
 '2025-06-06T10:00:00Z', '550e8400-e29b-41d4-a716-446655440009'),

-- Quiz 10
('0a000000-0000-0000-0000-000000000000', 'English Quiz', 'Grammar test', NULL,
 '2025-06-13T10:00:00Z', '2025-06-13T11:00:00Z', 3600000, 'engpass',
 TRUE, FALSE, TRUE, FALSE, FALSE, TRUE, TRUE, FALSE,
 '2025-06-06T11:00:00Z', '550e8400-e29b-41d4-a716-446655440009');




-- Course 2
INSERT INTO course (id, name, description, image, type, semester_id)
VALUES (
           '2a3b6c12-c9d8-43e2-983f-d736c0e71b2d',
           'Database Systems',
           'Concepts of relational databases and SQL.',
           'https://example.com/img2.png',
           'ELECTIVE',
           '8d0d1345-2ed6-4cd1-b020-f9c9ae71a3eb'
       );

-- Course 3
INSERT INTO course (id, name, description, image, type, semester_id)
VALUES (
           '3d4e7f13-ab23-4c12-aee0-3d7a1985b211',
           'Computer Networks',
           'Fundamentals of network protocols and architecture.',
           'https://example.com/img3.png',
           'CORE',
           '81c3f9a5-3322-4f12-a735-b8c7e637d450'
       );

-- Course 4
INSERT INTO course (id, name, description, image, type, semester_id)
VALUES (
           '4f6a8914-fe55-4ea7-b0cd-2ed88cd51b21',
           'Operating Systems',
           'Understanding the design of OS and process management.',
           'https://example.com/img4.png',
           'CORE',
           '6f7e1b15-753e-4c79-94d4-51e515a647631'
       );

-- Course 5
INSERT INTO course (id, name, description, image, type, semester_id)
VALUES (
           '5c9d1f15-aa33-4f2c-9d1a-47bdc490a2ff',
           'Machine Learning',
           'Introduction to ML algorithms and data preprocessing.',
           'https://example.com/img5.png',
           'ELECTIVE',
           '51c005b7-0f0e-42ed-a6f5-9e24c648a90f'
       );


INSERT INTO lab (id, name, block, ip_subnet)
VALUES
    ('11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Physics Lab', 'Block A', '192.168.10.0/24'),
    ('22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Chemistry Lab', 'Block B', '192.168.20.0/24'),
    ('33333333-cccc-cccc-cccc-cccccccccccc', 'Biology Lab', 'Block C', '192.168.30.0/24'),
    ('44444444-dddd-dddd-dddd-dddddddddddd', 'Computer Lab', 'Block D', '10.0.0.0/24'),
    ('55555555-eeee-eeee-eeee-eeeeeeeeeeee', 'Electronics Lab', 'Block E', '172.16.0.0/24');


INSERT INTO section (
    id,
    name,
    is_active,
    quiz_id
) VALUES (
             '23d79308-1010-4b8c-bf09-ec7cb9e9c2aa',
             'Introduction',
             true,
             '3a333333-3333-3333-3333-333333333333'
         );

-- Section 2
INSERT INTO section (
    id,
    name,
    is_active,
    quiz_id
) VALUES (
             'd9a89d3d-5ec0-4696-87e5-e94e9cde7b88',
             'Basic Concepts',
             true,
             '3a333333-3333-3333-3333-333333333333'
         );

-- Section 3
INSERT INTO section (
    id,
    name,
    is_active,
    quiz_id
) VALUES (
             'f8eab973-77c5-44b5-9e5d-5e90b5e5bd9d',
             'Advanced Topics',
             true,
             '3a333333-3333-3333-3333-333333333333'
         );

-- Section 4
INSERT INTO section (
    id,
    name,
    is_active,
    quiz_id
) VALUES (
             'e6fd2e91-2804-44f3-9fd3-c38ae8d6c310',
             'Case Studies',
             true,
             '7a777777-7777-7777-7777-777777777777'
         );

-- Section 5
INSERT INTO section (
    id,
    name,
    is_active,
    quiz_id
) VALUES (
             '2b43882a-f2d4-4a7d-bbb5-bba0c61248a6',
             'Final Review',
             true,
             '7a777777-7777-7777-7777-777777777777'
         );


