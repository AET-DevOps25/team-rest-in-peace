FROM grafana/grafana:latest

EXPOSE 3000

COPY provisioning /etc/grafana/provisioning

ENTRYPOINT ["sh", "-c", "/etc/grafana/provisioning/init_grafana.sh"]