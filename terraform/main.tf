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
  ami = "ami-0953476d60561c955" # Amazon Linux
  instance_type = "t3.medium"
  key_name      = "vockey"
  vpc_security_group_ids = [aws_security_group.allow_http_https.id]

  tags = {
    Name = "policy-watch"
  }

  # provisioner "local-exec" {
  #   command = "bash -c 'echo \"[ec2_instances]\" > ../ansible/inventory.ini && echo \"${self.public_ip}\" >> ../ansible/inventory.ini'"
  # }
}

resource "local_file" "ansible_inventory" {
  content = "[ec2_instances]\n${aws_instance.policy-watch.public_ip}\n"
  filename = "../ansible/inventory.ini"
  # Ensure this resource is created after the aws_instance is available
  depends_on = [aws_instance.policy-watch]
}

resource "aws_security_group" "allow_http_https" {
  name_prefix = "allow-http-https-"
  vpc_id      = "vpc-0d9520dc33adb20ea"

  ingress {
    from_port = 8000
    to_port   = 8000
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 8080
    to_port   = 8080
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 8081
    to_port   = 8081
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }


  ingress {
    from_port = 8082
    to_port   = 8082
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

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
    from_port = 9091
    to_port   = 9091
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 22
    to_port   = 22
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol = "-1" # All protocols
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "allow-http-https"
  }
}