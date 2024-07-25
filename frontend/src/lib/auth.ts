export function getAuthorizationUrl(): string {
  const { TWITTER_CLIENT_ID, TWITTER_CALLBACK_URL, TWITTER_SCOPE } = process.env;
  const scope = encodeURIComponent(TWITTER_SCOPE ?? '');
  const params = [
    `response_type=code`,
    `client_id=${TWITTER_CLIENT_ID}`,
    `redirect_uri=${TWITTER_CALLBACK_URL}`,
    `state=stat`,
    `code_challenge=challenge`,
    `code_challenge_method=plain`,
    `scope=${scope}`,
  ].join('&');

  return `https://twitter.com/i/oauth2/authorize?${params}`;
}
