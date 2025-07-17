FROM node:22-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

COPY package.json .

RUN npm install

RUN npm i -g serve

COPY . .

RUN npm run build

EXPOSE 3000

CMD [ "serve", "dist" ]
