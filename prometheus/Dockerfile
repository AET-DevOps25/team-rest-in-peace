from prom/prometheus:latest

EXPOSE 9090

COPY prometheus.k8s.yml /etc/prometheus/prometheus.yml

ENTRYPOINT [ "/bin/prometheus" ]
CMD [ "-config.file=/etc/prometheus/prometheus.yml", \
    "-storage.local.path=/prometheus", \
    "-web.console.libraries=/etc/prometheus/console_libraries", \
    "-web.console.templates=/etc/prometheus/consoles" ]
