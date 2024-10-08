import type { Metadata } from 'next';
import { ReactNode } from 'react';
import './globals.css';
import { Inter } from 'next/font/google';
import { cn } from '@/lib/utils';
import Navigation from '@/components/navigation';
import { Toaster } from '@/components/ui/toaster';
import Footer from '@/components/footer';

const inter = Inter({ subsets: ['latin'], variable: '--font-sans' });

export const metadata: Metadata = {
  title: 'Influencer AI',
  description: 'Generate ai-powered tweets for your social media accounts',
};

export default function RootLayout({ children }: Readonly<{ children: ReactNode }>) {
  return (
    <html lang="en">
      <body className={cn('min-h-screen bg-background font-sans antialiased', inter.variable)}>
        <header className="container flex items-center justify-between">
          <Navigation />
        </header>
        <main className="container">{children}</main>
        <Footer />
        <Toaster />
      </body>
    </html>
  );
}
