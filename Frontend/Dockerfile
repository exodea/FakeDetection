FROM node:15.11.0-alpine3.13

WORKDIR /frontend

COPY . .

RUN npm install

EXPOSE 80

CMD npm run serve -- --port 80