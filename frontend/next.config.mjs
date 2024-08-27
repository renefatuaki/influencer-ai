/** @type {import('next').NextConfig} */
const nextConfig = {
  images: {
    remotePatterns: [
      {
        protocol: 'http',
        hostname: '*',
        port: process.env.API_PORT,
      },
    ],
  },
};

export default nextConfig;
