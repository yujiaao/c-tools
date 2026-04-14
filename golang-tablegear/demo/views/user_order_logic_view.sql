SELECT
  u.id AS user_id,
  u.name AS user_name,
  o.order_no,
  o.amount,
  o.created_at
FROM user_account u
INNER JOIN order_info o ON o.user_id = u.id
WHERE u.status = 1

