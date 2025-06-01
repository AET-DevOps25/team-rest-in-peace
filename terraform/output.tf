output "public_aws_ip" {
  value       = aws_instance.policy-watch.public_ip
  description = "Public IP of the EC2 instance"
}