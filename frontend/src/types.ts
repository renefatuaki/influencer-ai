export enum Tone {
  AUTHORITATIVE = 'AUTHORITATIVE',
  EMPATHETIC = 'EMPATHETIC',
  FORMAL = 'FORMAL',
  FRIENDLY = 'FRIENDLY',
  INFORMATIVE = 'INFORMATIVE',
  INSTRUCTIVE = 'INSTRUCTIVE',
  JOKING = 'JOKING',
  PROFESSIONAL = 'PROFESSIONAL',
  SARCASTIC = 'SARCASTIC',
  SYMPATHETIC = 'SYMPATHETIC',
}

export enum Interest {
  ART = 'ART',
  BUSINESS = 'BUSINESS',
  CULTURE = 'CULTURE',
  EDUCATION = 'EDUCATION',
  ENTERTAINMENT = 'ENTERTAINMENT',
  ENVIRONMENT = 'ENVIRONMENT',
  FASHION = 'FASHION',
  FINANCE = 'FINANCE',
  FOOD = 'FOOD',
  HEALTH = 'HEALTH',
  HISTORY = 'HISTORY',
  HOBBIES = 'HOBBIES',
  HOLIDAYS = 'HOLIDAYS',
  HOME = 'HOME',
  HUMOR = 'HUMOR',
  LITERATURE = 'LITERATURE',
  MOVIES = 'MOVIES',
  MUSIC = 'MUSIC',
  NEWS = 'NEWS',
  POLITICS = 'POLITICS',
  RELIGION = 'RELIGION',
  SCIENCE = 'SCIENCE',
  SPORTS = 'SPORTS',
  TECHNOLOGY = 'TECHNOLOGY',
  TRAVEL = 'TRAVEL',
  TV = 'TV',
  VEHICLES = 'VEHICLES',
}

export enum BodyBuild {
  ATHLETIC = 'ATHLETIC',
  AVERAGE = 'AVERAGE',
  MUSCULAR = 'MUSCULAR',
  OVERWEIGHT = 'OVERWEIGHT',
  SLIM = 'SLIM',
}

export enum EyeColor {
  AMBER = 'AMBER',
  BLUE = 'BLUE',
  BROWN = 'BROWN',
  GRAY = 'GRAY',
  GREEN = 'GREEN',
  HAZEL = 'HAZEL',
  RED = 'RED',
  VIOLET = 'VIOLET',
}

export enum EyeShape {
  ALMOND = 'ALMOND',
  CLOSE_SET = 'CLOSE_SET',
  DEEP_SET = 'DEEP_SET',
  DOWNTURNED = 'DOWNTURNED',
  HOODED = 'HOODED',
  LARGE = 'LARGE',
  MONOLID = 'MONOLID',
  PROTRUDING = 'PROTRUDING',
  ROUND = 'ROUND',
  SMALL = 'SMALL',
  UPTURNED = 'UPTURNED',
  WIDE_SET = 'WIDE_SET',
}

export enum FaceFeatures {
  BEARD = 'BEARD',
  FRECKLES = 'FRECKLES',
  GOATEE = 'GOATEE',
  MOLE = 'MOLE',
  MUSTACHE = 'MUSTACHE',
  NONE = 'NONE',
  PIERCING = 'PIERCING',
  SCAR = 'SCAR',
  SIDE_BURNS = 'SIDE_BURNS',
  STUBBLE = 'STUBBLE',
  TATTOO = 'TATTOO',
}

export enum FaceShape {
  DIAMOND = 'DIAMOND',
  HEART = 'HEART',
  OVAL = 'OVAL',
  PEAR = 'PEAR',
  RECTANGLE = 'RECTANGLE',
  ROUND = 'ROUND',
  SQUARE = 'SQUARE',
  TRIANGLE = 'TRIANGLE',
}

export enum HairColor {
  BLACK = 'BLACK',
  BLONDE = 'BLONDE',
  BLUE = 'BLUE',
  BROWN = 'BROWN',
  GRAY = 'GRAY',
  GREEN = 'GREEN',
  ORANGE = 'ORANGE',
  PINK = 'PINK',
  PURPLE = 'PURPLE',
  RED = 'RED',
  WHITE = 'WHITE',
}

export enum Height {
  AVERAGE = 'AVERAGE',
  SHORT = 'SHORT',
  TALL = 'TALL',
}

export enum SkinTone {
  BLACK = 'BLACK',
  BROWN = 'BROWN',
  DARK = 'DARK',
  FAIR = 'FAIR',
  LIGHT = 'LIGHT',
  MEDIUM = 'MEDIUM',
  OLIVE = 'OLIVE',
}

export enum HairLength {
  BALD = 'BALD',
  LONG = 'LONG',
  MEDIUM = 'MEDIUM',
  SHORT = 'SHORT',
}

export enum Style {
  ARTSY = 'ARTSY',
  ATHLETIC = 'ATHLETIC',
  BOHO = 'BOHO',
  BUSINESS = 'BUSINESS',
  CASUAL = 'CASUAL',
  CHIC = 'CHIC',
  COSPLAY = 'COSPLAY',
  COUNTRY = 'COUNTRY',
  CYBERPUNK = 'CYBERPUNK',
  ELEGANT = 'ELEGANT',
  EMO = 'EMO',
  FANTASY = 'FANTASY',
  FORMAL = 'FORMAL',
  FUTURISTIC = 'FUTURISTIC',
  GOTH = 'GOTH',
  GOTHIC_LITA = 'GOTHIC_LITA',
  GRUNGE = 'GRUNGE',
  GYPSY = 'GYPSY',
  HIPSTER = 'HIPSTER',
  HISTORICAL = 'HISTORICAL',
  KAWAII = 'KAWAII',
  MILITARY = 'MILITARY',
  PARTY = 'PARTY',
  PREPPY = 'PREPPY',
  PUNK = 'PUNK',
  RETRO = 'RETRO',
  RUGGED = 'RUGGED',
  SCI_FI = 'SCI_FI',
  SKATER = 'SKATER',
  SPORT = 'SPORT',
  STEAMPUNK = 'STEAMPUNK',
  SURFER = 'SURFER',
  URBAN = 'URBAN',
  VINTAGE = 'VINTAGE',
  VISUAL_KEI = 'VISUAL_KEI',
  WESTERN = 'WESTERN',
}

export type Twitter = {
  id: string;
  name: string;
  username: string;
  auth: {
    isAuthorized: boolean;
  };
};

export type Personality = {
  tone: Tone[];
  interest: Interest[];
};

export type Appearance = {
  bodyBuild: BodyBuild;
  eyeColor: EyeColor;
  eyeShape: EyeShape;
  faceFeatures: FaceFeatures[];
  faceShape: FaceShape;
  hairColor: HairColor;
  hairLength: HairLength;
  height: Height;
  skinTone: SkinTone;
  style: Style;
};

export type Influencer = {
  id: string;
  twitter: Twitter;
  personality: Personality;
  appearance: Appearance;
};

export type Pagination = {
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  size: number;
  content: Influencer[];
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  numberOfElements: number;
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  empty: boolean;
};
