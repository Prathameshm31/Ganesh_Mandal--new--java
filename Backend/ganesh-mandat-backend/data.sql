USE ganesh_mandal;

INSERT INTO members (name, mobile, address, last_year_amount) VALUES
('Ramesh Patil', '9876543210', 'House No. 123, Shivaji Nagar, Pune', 5000.00),
('Suresh Deshmukh', '9876543211', 'House No. 456, Koregaon Park, Pune', 3000.00),
('Anita Joshi', '9876543212', 'House No. 789, Kothrud, Pune', 7500.00),
('Vijay Kulkarni', '9876543213', 'House No. 101, Baner, Pune', 2000.00),
('Pooja Shinde', '9876543214', 'House No. 202, Hadapsar, Pune', 4500.00),
('Amit Gavade', '9876543215', 'House No. 303, Viman Nagar, Pune', 6000.00),
('Sunita More', '9876543216', 'House No. 404, Deccan Gymkhana, Pune', 3500.00),
('Rahul Jagtap', '9876543217', 'House No. 505, Wagholi, Pune', 8000.00),
('Deepa Kale', '9876543218', 'House No. 606, Pimple Saudagar, Pune', 2500.00),
('Sanjay Bhoite', '9876543219', 'House No. 707, Laxmi Road, Pune', 10000.00);

INSERT INTO collections (member_id, amount, payment_mode, transaction_id, collection_date, remarks) VALUES
(1, 2000.00, 'CASH', NULL, '2025-09-15', 'Door-to-door collection'),
(1, 1500.00, 'ONLINE', 'TXN001', '2025-09-20', 'Online payment via UPI'),
(2, 3000.00, 'CASH', NULL, '2025-09-16', 'Festival donation'),
(3, 5000.00, 'ONLINE', 'TXN002', '2025-09-17', 'Bank transfer'),
(3, 2500.00, 'CASH', NULL, '2025-09-18', 'Collected at mandal'),
(4, 2000.00, 'ONLINE', 'TXN003', '2025-09-19', 'Google Pay'),
(5, 4500.00, 'CASH', NULL, '2025-09-20', 'Door-to-door'),
(6, 3000.00, 'CASH', NULL, '2025-09-21', 'Mandal visit'),
(6, 3000.00, 'ONLINE', 'TXN004', '2025-09-22', 'PhonePe'),
(7, 3500.00, 'CASH', NULL, '2025-09-23', 'Festival special'),
(8, 5000.00, 'ONLINE', 'TXN005', '2025-09-24', 'Online donation'),
(9, 2500.00, 'CASH', NULL, '2025-09-25', 'Door-to-door'),
(10, 6000.00, 'CASH', NULL, '2025-09-26', 'Sponsorship'),
(10, 4000.00, 'ONLINE', 'TXN006', '2025-09-27', 'Bank transfer');
