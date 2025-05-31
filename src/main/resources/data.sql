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
                                                                                                                             ('550e8400-e29b-41d4-a716-446655440009', 'Jake Brown', 'jake.brown@example.com', 'jakeb', 'https://example.com/images/jake.jpg', 2, '4443332221', FALSE, CURRENT_TIMESTAMP, NULL);
INSERT INTO semester(id,name,year,is_active) VALUES(gen_random_uuid(),'AIE 2024',2024,true);
INSERT INTO semester(id,name,year,is_active) VALUES(gen_random_uuid(),'AIDS 2024',2024,true);
INSERT INTO semester(id,name,year,is_active) VALUES(gen_random_uuid(),'MECH 2024',2024,true);
INSERT INTO semester(id,name,year,is_active) VALUES(gen_random_uuid(),'CSE 2024',2024,true);
INSERT INTO semester(id,name,year,is_active) VALUES(gen_random_uuid(),'CIVIL 2024',2024,true);
-- 1. AI Fundamentals - Semester 1
INSERT INTO bank (
    id,
    name,
    description,
    semester,
    created_at,
    created_by_id
) VALUES (
             gen_random_uuid(),
             'AI Fundamentals',
             'Introduction to Artificial Intelligence concepts and applications.',
             1,
             NOW(),
             '550e8400-e29b-41d4-a716-446655440001'
         );

-- 2. Machine Learning Basics - Semester 2
INSERT INTO bank (
    id,
    name,
    description,
    semester,
    created_at,
    created_by_id
) VALUES (
             gen_random_uuid(),
             'Machine Learning Basics',
             'Bank of foundational questions for ML models and algorithms.',
             2,
             NOW(),
             '550e8400-e29b-41d4-a716-446655440001'
         );

-- 3. Data Structures - Semester 3
INSERT INTO bank (
    id,
    name,
    description,
    semester,
    created_at,
    created_by_id
) VALUES (
             gen_random_uuid(),
             'Data Structures',
             'Questions related to arrays, stacks, queues, linked lists, trees, and graphs.',
             3,
             NOW(),
             '550e8400-e29b-41d4-a716-446655440001'
         );

-- 4. Database Systems - Semester 4
INSERT INTO bank (
    id,
    name,
    description,
    semester,
    created_at,
    created_by_id
) VALUES (
             gen_random_uuid(),
             'Database Systems',
             'SQL, normalization, transactions, and relational algebra concepts.',
             4,
             NOW(),
             '550e8400-e29b-41d4-a716-446655440001'
         );

-- 5. Operating Systems - Semester 5
INSERT INTO bank (
    id,
    name,
    description,
    semester,
    created_at,
    created_by_id
) VALUES (
             gen_random_uuid(),
             'Operating Systems',
             'Concurrency, memory management, file systems, and scheduling algorithms.',
             5,
             NOW(),
             '550e8400-e29b-41d4-a716-446655440001'
         );
