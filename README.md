# Membership Program Service

A comprehensive membership management system with tier-based benefits, automatic evaluation, and transaction tracking.

## Features

- **Membership Plans**: Monthly, Quarterly, and Annual subscription options
- **Tier System**: Bronze, Silver, Gold, and Platinum tiers with automatic evaluation
- **Benefits Management**: Configurable benefits for each tier
- **Transaction Tracking**: Complete audit trail of membership changes
- **Automatic Tier Evaluation**: Scheduled evaluation based on user activity
- **Data Initialization**: Automatic setup with sample data

## Data Initialization

The application includes automatic data initialization that populates the database with sample data for testing and development purposes.

### Configuration

Data initialization is controlled by the following properties in `application.yml`:

```yaml
app:
  data:
    initialization:
      enabled: true   # Set to false to disable data initialization
      force: false    # Set to true to force initialization even if data exists
```

### What Gets Initialized

The system automatically creates:

1. **Membership Plans**:
   - Monthly Premium ($49.99)
   - Quarterly Premium ($129.99)
   - Annual Premium ($499.99)

2. **Membership Tiers**:
   - Bronze (Level 1) - Basic benefits
   - Silver (Level 2) - Enhanced benefits
   - Gold (Level 3) - Premium benefits
   - Platinum (Level 4) - VIP benefits

3. **Tier Benefits**:
   - Bronze: Free delivery on orders over $50
   - Silver: 5% discount + Priority support
   - Gold: 2% cashback + Early access
   - Platinum: Exclusive deals + Faster delivery

4. **Tier Criteria**:
   - Silver: 10 orders or $500 spending in 90 days
   - Gold: $2000 lifetime spending + 1 year membership
   - Platinum: VIP customer cohort membership

5. **Sample Memberships**:
   - User 1001: Annual Premium + Gold tier (active)
   - User 1002: Monthly Premium + Bronze tier (active)
   - User 1003: Annual Premium + Bronze tier (pending payment)

6. **Sample Transactions**:
   - Initial subscriptions
   - Tier upgrades
   - Payment records

### How It Works

1. **First Run**: When the application starts for the first time, it automatically executes the `data.sql` script
2. **Subsequent Runs**: The system checks if data already exists and skips initialization
3. **Force Mode**: Set `app.data.initialization.force=true` to reinitialize even if data exists
4. **Disable**: Set `app.data.initialization.enabled=false` to completely disable initialization

### Database Setup

The system uses PostgreSQL with the following configuration:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/membership_db
    username: membership_user
    password: membership_pass
  jpa:
    hibernate:
      ddl-auto: update
    defer-datasource-initialization: true
  sql:
    init:
      mode: always
      data-locations: classpath:data.sql
```

### Sample Data Usage

After initialization, you can test the API with the following sample user IDs:
- **User 1001**: Gold tier member with annual plan
- **User 1002**: Bronze tier member with monthly plan
- **User 1003**: Bronze tier member with pending payment

### API Endpoints

The system provides RESTful endpoints for:
- Membership management (subscribe, upgrade, cancel)
- Tier evaluation and updates
- Transaction history
- Plan and tier information

### Development

To run the application:

1. Start PostgreSQL database
2. Update database connection in `application.yml`
3. Run the application: `./mvnw spring-boot:run`
4. Data will be automatically initialized on first run

### Testing

The data initialization is excluded from the test profile to prevent interference with unit tests. Test data should be managed separately in test configurations.
