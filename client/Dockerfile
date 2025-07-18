FROM node:22-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

COPY package.json .

RUN npm install

RUN npm i -g serve

COPY . .

# Build the application
RUN npm run build

# Copy runtime env generator script
COPY generate-env.cjs .

EXPOSE 3000

# Run env generator, then serve
CMD ["sh", "-c", "node generate-env.cjs && serve -s dist -l 3000"]