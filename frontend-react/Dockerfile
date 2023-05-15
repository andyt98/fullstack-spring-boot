FROM node:19-alpine
ARG api_base_url
WORKDIR /app
COPY . .
RUN npm i --silent && echo "VITE_API_BASE_URL=${api_base_url}" > .envdo
EXPOSE 5173
CMD ["npm", "run", "dev"]