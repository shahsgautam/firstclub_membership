-- Data initialization script for membership program
-- This script will be executed automatically by Spring Boot if spring.jpa.hibernate.ddl-auto is set to create or create-drop

-- Insert Membership Plans
INSERT INTO membership_plans (name, duration, price, description, active, created_at, updated_at) VALUES
('Monthly Premium', 'MONTHLY', 49.99, 'Access to all gym facilities, classes, and equipment', true, NOW(), NOW()),
('Quarterly Premium', 'QUARTERLY', 129.99, '3-month commitment with slight discount', true, NOW(), NOW()),
('Annual Premium', 'YEARLY', 499.99, 'Best value - 2 months free with annual commitment', true, NOW(), NOW());

-- Insert Membership Tiers
INSERT INTO membership_tiers (name, level, description, active, created_at, updated_at) VALUES
('Bronze', 1, 'Basic member benefits', true, NOW(), NOW()),
('Silver', 2, 'Enhanced benefits for regular customers', true, NOW(), NOW()),
('Gold', 3, 'Premium benefits for high-value customers', true, NOW(), NOW()),
('Platinum', 4, 'Exclusive benefits for VIP customers', true, NOW(), NOW());

-- Insert Tier Benefits
INSERT INTO tier_benefits (tier_id, type, value, description, active) VALUES
-- Bronze Tier Benefits
((SELECT id FROM membership_tiers WHERE name = 'Bronze'), 'FREE_DELIVERY', 0.00, 'Free delivery on orders over $50', true),

-- Silver Tier Benefits
((SELECT id FROM membership_tiers WHERE name = 'Silver'), 'PERCENTAGE_DISCOUNT', 5.00, '5% discount on all purchases', true),
((SELECT id FROM membership_tiers WHERE name = 'Silver'), 'PRIORITY_SUPPORT', NULL, 'Priority customer support', true),

-- Gold Tier Benefits
((SELECT id FROM membership_tiers WHERE name = 'Gold'), 'CASHBACK', 2.00, '2% cashback on all purchases', true),
((SELECT id FROM membership_tiers WHERE name = 'Gold'), 'EARLY_ACCESS', NULL, 'Early access to sales and new products', true),

-- Platinum Tier Benefits
((SELECT id FROM membership_tiers WHERE name = 'Platinum'), 'EXCLUSIVE_DEALS', NULL, 'Exclusive deals and limited edition products', true),
((SELECT id FROM membership_tiers WHERE name = 'Platinum'), 'FASTER_DELIVERY', NULL, 'Express delivery on all orders', true);

-- Insert Tier Criteria
INSERT INTO tier_criteria (tier_id, type, threshold, evaluation_period_days, description, active) VALUES
-- Silver Tier Criteria
((SELECT id FROM membership_tiers WHERE name = 'Silver'), 'MIN_ORDER_COUNT', 10, 90, 'Minimum 10 orders in 90 days', true),
((SELECT id FROM membership_tiers WHERE name = 'Silver'), 'MIN_ORDER_VALUE', 500.00, 90, 'Minimum $500 total spending in 90 days', true),

-- Gold Tier Criteria
((SELECT id FROM membership_tiers WHERE name = 'Gold'), 'CUMULATIVE_SPENDING', 2000.00, NULL, 'Lifetime spending over $2,000', true),
((SELECT id FROM membership_tiers WHERE name = 'Gold'), 'MEMBERSHIP_DURATION', 365, NULL, 'Member for at least 1 year', true),

-- Platinum Tier Criteria
((SELECT id FROM membership_tiers WHERE name = 'Platinum'), 'USER_COHORT', NULL, NULL, 'Part of VIP customer cohort', true);

-- Insert Sample User Memberships
INSERT INTO user_memberships (user_id, plan_id, tier_id, status, start_date, end_date, auto_renew, created_at, updated_at, version) VALUES
(1001, (SELECT id FROM membership_plans WHERE name = 'Annual Premium'), (SELECT id FROM membership_tiers WHERE name = 'Gold'), 'ACTIVE', NOW() - INTERVAL '6 months', NOW() + INTERVAL '6 months', true, NOW(), NOW(), 0),
(1002, (SELECT id FROM membership_plans WHERE name = 'Monthly Premium'), (SELECT id FROM membership_tiers WHERE name = 'Bronze'), 'ACTIVE', NOW() - INTERVAL '15 days', NOW() + INTERVAL '15 days', true, NOW(), NOW(), 0),
(1003, (SELECT id FROM membership_plans WHERE name = 'Annual Premium'), (SELECT id FROM membership_tiers WHERE name = 'Bronze'), 'PENDING_PAYMENT', NOW() - INTERVAL '5 days', NOW() + INTERVAL '11 months 25 days', false, NOW(), NOW(), 0);

-- Insert Sample Transactions
INSERT INTO membership_transactions (membership_id, type, amount, new_plan_id, new_tier_id, notes, transaction_date) VALUES
-- Initial subscription for user 1001
((SELECT id FROM user_memberships WHERE user_id = 1001), 'SUBSCRIPTION', 499.99, (SELECT id FROM membership_plans WHERE name = 'Annual Premium'), (SELECT id FROM membership_tiers WHERE name = 'Bronze'), 'Initial yearly subscription', NOW() - INTERVAL '6 months'),

-- Tier upgrade for user 1001
((SELECT id FROM user_memberships WHERE user_id = 1001), 'TIER_CHANGE', 0.00, NULL, (SELECT id FROM membership_tiers WHERE name = 'Gold'), 'Automatic tier upgrade based on spending criteria', NOW() - INTERVAL '3 months'),

-- Initial subscription for user 1002
((SELECT id FROM user_memberships WHERE user_id = 1002), 'SUBSCRIPTION', 49.99, (SELECT id FROM membership_plans WHERE name = 'Monthly Premium'), (SELECT id FROM membership_tiers WHERE name = 'Bronze'), 'Initial monthly subscription', NOW() - INTERVAL '15 days');
