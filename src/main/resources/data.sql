-- data.sql - Sample data for testing
INSERT INTO form_submissions (name, email, address, phone_number, comments, status, submitted_by_ip) VALUES
('John Doe', 'john.doe@example.com', '123 Main St, New York, NY 10001', '+1-555-0123', 'Looking forward to hearing from you!', 'PENDING', '192.168.1.100'),
('Jane Smith', 'jane.smith@example.com', '456 Oak Ave, Los Angeles, CA 90210', '+1-555-0124', 'Please contact me during business hours.', 'APPROVED', '192.168.1.101'),
('Bob Johnson', 'bob.johnson@example.com', '789 Pine Rd, Chicago, IL 60601', '+1-555-0125', NULL, 'PENDING', '192.168.1.102'),
('Alice Brown', 'alice.brown@example.com', '321 Elm St, Houston, TX 77001', NULL, 'Interested in your services.', 'REJECTED', '192.168.1.103'),
('Charlie Wilson', 'charlie.wilson@example.com', '654 Maple Dr, Phoenix, AZ 85001', '+1-555-0126', 'Thank you for the opportunity.', 'PROCESSING', '192.168.1.104');