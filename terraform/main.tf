terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.16"
    }
  }

  required_version = ">= 1.2.0"
}

provider "aws" {
  region  = "us-east-1"
  profile = "default"
}

resource "aws_instance" "policy-watch" {
  ami = "ami-084568db4383264d4" # Ubuntu
  instance_type = "t2.micro"
  key_name = "vockey"
  vpc_security_group_ids = [aws_security_group.allow_http_https.id]

  tags = {
    Name = "policy-watch"
  }
}

resource "aws_security_group" "allow_http_https" {
  name_prefix = "allow-http-https-"
  vpc_id      = "vpc-0d9520dc33adb20ea"

  ingress {
    from_port = 80
    to_port   = 80
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 443
    to_port   = 443
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 22
    to_port   = 22
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Consider limiting this to your IP
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "allow-http-https"
  }
}