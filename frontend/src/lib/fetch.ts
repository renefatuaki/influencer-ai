'use server';

const { API } = process.env;

function removeLeadingSlash(path: string) {
  return path.startsWith('/') ? path.slice(1) : path;
}

async function parseResponse(response: Response) {
  if (response.status === 204 || response.headers.get('Content-Length') === '0') return { status: response.status };

  return { status: response.status, ...(await response.json()) };
}

export async function GET(path: string, cache?: RequestCache, headers?: HeadersInit) {
  const sanitizedPath = removeLeadingSlash(path);

  const response = await fetch(`${API}/${sanitizedPath}`, {
    method: 'GET',
    cache: cache ?? 'force-cache',
    headers: {
      ...headers,
    },
  });

  return await response.json();
}

export async function POST(path: string, body: object, headers?: HeadersInit) {
  const sanitizedPath = removeLeadingSlash(path);

  const response = await fetch(`${API}/${sanitizedPath}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...headers,
    },
    body: JSON.stringify(body),
  });

  return parseResponse(response);
}

export async function DELETE(path: string, headers?: HeadersInit) {
  const sanitizedPath = removeLeadingSlash(path);

  const response = await fetch(`${API}/${sanitizedPath}`, {
    method: 'DELETE',
    headers: {
      ...headers,
    },
  });

  return await parseResponse(response);
}

export async function PUT(path: string, body: object, headers?: HeadersInit) {
  const sanitizedPath = removeLeadingSlash(path);

  const response = await fetch(`${API}/${sanitizedPath}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      ...headers,
    },
    body: JSON.stringify(body),
  });

  return await parseResponse(response);
}

export async function PATCH(path: string, body: object, headers?: HeadersInit) {
  const sanitizedPath = removeLeadingSlash(path);

  const response = await fetch(`${API}/${sanitizedPath}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      ...headers,
    },
    body: JSON.stringify(body),
  });

  return await parseResponse(response);
}
