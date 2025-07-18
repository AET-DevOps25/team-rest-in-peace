# Policy Watch - Setup Instructions

This document provides instructions for setting up and running the Policy Watch project both locally and on AWS.

## Local Setup

### Prerequisites

- Docker and Docker Compose installed on your machine
- Git to clone the repository

### Setup Steps

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd team-rest-in-peace
   ```

2. **Create a `.env` file in the root directory:**
   ```bash
   cp .env.template .env
   ```

   Then edit the `.env` file and configure the following environment variables:
   ```
   POSTGRES_PASSWORD
   DF_BUNDESTAG_API_KEY (-> https://dip.bundestag.de/%C3%BCber-dip/hilfe/api)
   DF_DB_PASSWORD
   NLP_DB_PASSWORD
   NLP_GENAI_API_KEY (-> Gemini-API-Key)
   BS_DB_PASSWORD
   NS_MAIL_PASSWORD (-> Google App PW)
   ```

3. **Build and start the services:**
   ```bash
   docker compose up --build
   ```

4. **Access the application:**
   - The client application will be available at http://localhost:80
   - Grafana dashboard will be available at http://localhost:9091
   - OpenAPI documentation (SwaggerUI) will be available at http://localhost:80/docs

## AWS Deployment

### Prerequisites

- AWS CLI installed and configured
- Terraform installed
- Ansible installed
- SSH key for accessing EC2 instances

### Setup Steps

1. **Configure AWS credentials:**
   ```bash
   aws configure
   ```

   Alternatively, ensure your AWS credentials are set up in `~/.aws/credentials`:
   ```
   [default]
   aws_access_key_id=YOUR_ACCESS_KEY
   aws_secret_access_key=YOUR_SECRET_KEY
   ```

2. **Create a `.env` file** in the root directory as described in the Local Setup section.

3. **Deploy the infrastructure using Terraform:**
   ```bash
   cd terraform
   terraform init
   terraform apply -auto-approve
   cd ..
   ```

4. **Create a new .env.aws file:**
   ```bash
   # Read the new base URL from the 2nd line of inventory.ini
   NEW_URL=$(sed -n '2p' ansible/inventory.ini | sed 's/^/http:\/\//; s/$/\//')
   # Copy everything from .env to .env.aws, replacing CLIENT_BASEURL
   sed "s|^CLIENT_BASE_URL=.*|CLIENT_BASE_URL=${NEW_URL}|" .env > .env.aws
   ```

5. **Deploy the application using Ansible:**
   - Replace `[SSH_PKEY_PATH]` with the path to your SSH private key for AWS (e.g., `~/.ssh/labuser.pem`).
   ```bash
   cd ansible
   ansible-playbook --private-key=[SSH_PKEY_PATH] playbook.yml
   cd ..
   ```

6. **Access the application:**
   - The application will be available at the IP address listed in `ansible/inventory.ini`
   - You can view the IP with:
   ```bash
   source <(grep '^CLIENT_BASE_URL=' .env.aws)
   echo "${CLIENT_BASE_URL}"
   ```

## Troubleshooting

### Local Deployment

- If services fail to start, check the Docker logs: `docker compose logs`
- Ensure all required ports are available on your machine
- Verify that the `.env` file contains all required variables

### AWS Deployment

- If Terraform fails, check the error messages and AWS console for more details
- If Ansible fails, try running with `-vvv` flag for verbose output:
  ```bash
  ansible-playbook -vvv --private-key=[SSH_PKEY_PATH] ansible/playbook.yml
  ```
- Check EC2 instance security groups to ensure all required ports are open
- Verify that the SSH key has the correct permissions: `chmod 400 [YOUR_SSH_PKEY]`
