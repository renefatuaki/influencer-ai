# Capstone Project - Influencer AI

## Table of Contents

* [About](#-about)
* [Features](#-features)
* [API](#-api)
* [Setup](#-setup)
* [Usage](#-usage)
* [Feedback and Contributions](#-feedback-and-contributions)

## 💡 About

The Influencer AI platform is designed to streamline the creation and management of AI-driven Twitter bot accounts. It enables users to fully customize bot
personalities, define visual appearances, and set up tweet scheduling. Leveraging Mistral AI through a Dockerized Ollama instance, the platform generates tweets
that are contextually relevant and align with the chosen personas. For visual content, Stability Diffusion is employed to create images tailored to each bot’s
identity. The platform also includes a comprehensive approval process for images, ensuring that visual content aligns with the accompanying tweets before they
are posted.

## 🎯 Features

* Authorization of Twitter accounts using OAuth 2.0 authentication with comprehensive access control.
* AI-generated tweets leveraging Mistral AI through a Dockerized Ollama instance to produce contextually relevant and stylistically aligned posts.
* AI-generated images integrated with Stability Diffusion for creating visuals tailored to influencer personas.
* Post scheduling and automation with flexible time and day-of-week selection for automated posting.
* Content approval process that includes a workflow to review, approve, or modify generated images before publishing.
* Influencer personality customization allowing users to define distinct personas, styles, and preferences for each bot, impacting both visual and textual
  content.

## 🔌 API

* **Twitter API** using OAuth 2.0 integration to manage bot accounts, post tweets, and retrieve user interactions.
* **Stability Diffusion** using a RESTful integration for generating influencer profile images and content images based on defined characteristics.

## ⚙️ Setup

This project is fully containerized, including the backend, frontend, database, and Ollama services. To get started, you’ll need to configure the necessary
environment variables and ensure Docker is properly set up.

**Prerequisites**

1. Twitter Developer Account: Obtain API keys and tokens from your Twitter Developer Account.
2. Stability Diffusion API Access: Ensure you have access to the Stability Diffusion API for image generation.
3. Docker Configuration: Ensure Docker is configured with at least 10 GB of memory to accommodate the required services.

### Step 1: Set Up Environment Variables

Populate the following files with your environment variables:

* `.env.backend`
* `.env.frontend`

These files should include all the required credentials, such as your Twitter API keys and Stability Diffusion API credentials.

### Step 2: Build and Launch Services

Once your environment variables are set, you can build and launch the application using Docker Compose. Run the following command in your terminal:

```sh
docker-compose up --build
```

**Note:** The build process may take several minutes, as Ollama will download and install the Mistral LLM during the initial setup.

## 📘 Usage

### Starting the Application:

* Once the services are running via Docker, access the frontend by navigating to [127.0.0.1:3000/management](http://127.0.0.1:3000/management) in your web
  browser.
* Click on ‘Add Influencer’ to authorize the application to access one of your Twitter bot accounts.

### Configuring Bot Accounts:

* Navigate to the management tab and select an influencer by clicking the three dots next to their name.
* Choose ‘Edit’ to enter the bot configuration interface. If you’ve just authorized an account, you will be redirected automatically.
* Define bot personalities, set visual profiles, and schedule tweet postings according to your preferences.

### Content Approval Workflow:

* Review AI-generated images before they are published using the platform’s intuitive interface.
* Approve or reject images, with the option to regenerate them while keeping the associated tweet intact.

### Activity:

* View recent tweets in the “Activity Log” tab to monitor bot activity and engagement.

### Scheduling Tweets:

* Utilize the scheduling feature to automate tweet postings.
* Select specific times and days for tweets, ensuring consistent and timely activity on your bot accounts.

## 🤝 Feedback and Contributions

> [!IMPORTANT]
> Whether you have feedback on features, have encountered any bugs, or have suggestions for enhancements, I'm eager to hear from you.

Please feel free to contribute by submitting an issue.