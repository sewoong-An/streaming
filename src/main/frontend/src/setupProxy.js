const { createProxyMiddleware } = require("http-proxy-middleware");

module.exports = function (app) {
  app.use(
    ["/api", "/member"],
    createProxyMiddleware({
      target: "https://vexel-sw.shop",
      // target: "http://localhost:8080",
      changeOrigin: true,
    })
  );
};
