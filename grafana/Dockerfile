FROM grafana/grafana:latest

EXPOSE 3000

COPY provisioning /etc/grafana/provisioning

ENTRYPOINT ["/etc/grafana/provisioning/init_grafana.sh"]