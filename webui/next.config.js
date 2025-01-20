/** @type {import('next').NextConfig} */
const rewrites = async () => {
  return [
    {
      source: "/api/:path*",
      destination: "http://localhost:8080/api/:path*",
    },
  ];
};
const nextConfig = {
  rewrites,
};

module.exports = nextConfig;
