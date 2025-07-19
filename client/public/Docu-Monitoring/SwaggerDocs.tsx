import { useEffect, useRef } from "react";

import "swagger-ui-dist/swagger-ui.css";

const SwaggerDocs = () => {
  const swaggerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (typeof window === "undefined") return;
    const loadSwagger = async () => {
      const SwaggerUIBundle = (
        await import("swagger-ui-dist/swagger-ui-bundle.js")
      ).default;
      const SwaggerUIStandalonePreset = (
        await import("swagger-ui-dist/swagger-ui-standalone-preset.js")
      ).default;

      SwaggerUIBundle({
        urls: [
          { url: "/openapi/genai.json", name: "GenAI Service" },
          { url: "/openapi/notification.json", name: "Notification Service" },
          { url: "/openapi/data-fetching.json", name: "Data Fetching Service" },
          { url: "/openapi/browsing.json", name: "Browsing Service" },
        ],
        dom_id: "#swagger-ui",
        deepLinking: true,
        presets: [SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset],
        plugins: [SwaggerUIBundle.plugins.DownloadUrl],
        layout: "StandaloneLayout",
      });
    };
    loadSwagger();
  }, []);

  return <div id="swagger-ui" ref={swaggerRef} />;
};

export default SwaggerDocs;
