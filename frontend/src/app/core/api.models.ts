export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface Plant {
  id: number;
  scientificName: string;
  displayName: string;
  family?: string | null;
  description?: string | null;
  nameRu?: string | null;
  nameKk?: string | null;
  synonyms?: string | null;
  distributionRegions?: string | null;
  usedParts?: string | null;
  reviewStatus: ReviewStatus;
  createdAt: string;
  updatedAt: string;
}

export interface PlantSample {
  id: number;
  plantId: number;
  scientificName: string;
  plantPart?: string | null;
  origin?: string | null;
  batchIdentifier?: string | null;
  collectedOn?: string | null;
  metadata?: string | null;
  createdAt: string;
  extracts?: Extract[];
}

export interface Extract {
  id: number;
  extractionMethod?: string | null;
  solvent?: string | null;
  conditions?: string | null;
  batchIdentifier?: string | null;
}

export interface Source {
  id: number;
  title: string;
  authors?: string | null;
  publicationYear?: number | null;
  doi?: string | null;
  url?: string | null;
  sourceType: string;
  licenseNote?: string | null;
}

export interface PlantOccurrence {
  plantId: number;
  scientificName: string;
  displayName: string;
  plantPart?: string | null;
  sampleOrBatch?: string | null;
  sampleId?: number | null;
  extractId?: number | null;
  source?: Source | null;
  notes?: string | null;
}

export interface CompoundSummary {
  id: number;
  name: string;
  canonicalSmiles: string;
  inchiKey?: string | null;
  externalId?: string | null;
  molecularFormula?: string | null;
  structureStatus: string;
}

export interface CompoundDetails extends CompoundSummary {
  createdAt: string;
  updatedAt: string;
  plantOccurrences: PlantOccurrence[];
}

export interface Target {
  id: number;
  normalizedIdentifier: string;
  name: string;
  organism?: string | null;
}

export interface Assay {
  id: number;
  activityCode: string;
  name: string;
  targetName?: string | null;
  assayType?: string | null;
  description?: string | null;
  standardUnit?: string | null;
  target?: Target | null;
}

export interface ActivityMeasurement {
  id: number;
  compoundId?: number | null;
  extract?: {
    id: number;
    method?: string | null;
    solvent?: string | null;
    conditions?: string | null;
    batchIdentifier?: string | null;
    sample: {
      id: number;
      plantId: number;
      plantScientificName: string;
      plantPart?: string | null;
      origin?: string | null;
      batchIdentifier?: string | null;
    };
  } | null;
  assay: Assay;
  source: Source;
  value: number;
  unit: string;
  relation: string;
  experimentalConditions?: string | null;
  qualityStatus: string;
  createdAt: string;
}

export interface PredictionRequest {
  smiles: string;
  activityCode: string;
  compoundId?: number | null;
}

export interface Prediction {
  predictionId: number;
  status: string;
  activityCode: string;
  inputSmiles: string;
  predictedLabel?: string | null;
  predictedValue?: number | null;
  probability?: number | null;
  confidenceLevel?: string | null;
  modelName: string;
  modelVersion: string;
  isDemo: boolean;
  uncertainty?: number | null;
  modelExecutionType: string;
  modelDatasetDescription?: string | null;
  modelMetrics?: string | null;
  warning: string;
  createdAt: string;
}

export interface CandidateComparison {
  compound: CompoundSummary;
  measurements: ActivityMeasurement[];
}

export interface ComparisonResponse {
  activityCode: string;
  notice: string;
  candidates: CandidateComparison[];
}

export interface ApiError {
  timestamp: string;
  status: number;
  errorCode: string;
  message: string;
  path: string;
  fieldErrors: { field: string; message: string }[];
}

export type UserRole = 'USER' | 'EXPERT' | 'ADMIN';

export interface UserAccountView {
  id: number;
  email: string;
  displayName: string;
  role: UserRole;
  createdAt: string;
}

export interface RegisterRequest {
  email: string;
  displayName: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export type ClaimType = 'INDICATION' | 'CONTRAINDICATION' | 'INTERACTION';
export type ReviewStatus = 'UNVERIFIED' | 'PENDING' | 'VERIFIED' | 'REJECTED';

export interface KnowledgeClaim {
  id: number;
  plantId?: number | null;
  plantName?: string | null;
  preparationId?: number | null;
  preparationName?: string | null;
  claimType: ClaimType;
  title: string;
  claimText: string;
  interactionPartner?: string | null;
  reviewStatus: ReviewStatus;
  source: Source;
  sourceLocator?: string | null;
  reviewedAt?: string | null;
}

export interface ClaimReviewRequest {
  reviewStatus: 'VERIFIED' | 'REJECTED';
  reviewNote?: string;
}

export interface CreateSourceRequest {
  title: string;
  authors?: string | null;
  publicationYear?: number | null;
  doi?: string | null;
  url?: string | null;
  sourceType: 'ARTICLE' | 'REGISTRY' | 'MONOGRAPH' | 'DATASET' | 'OTHER';
  licenseNote?: string | null;
}

export interface AssistantRequest {
  question: string;
  conversationId?: number;
}

export interface AssistantCitation {
  claimId: number;
  title: string;
  claimText: string;
  subject?: string | null;
  sourceLocator?: string | null;
  source: Source;
}

export interface AssistantResponse {
  conversationId: number;
  answer: string;
  status: 'SAFETY_REDIRECT' | 'NO_EVIDENCE' | 'EVIDENCE_FOUND';
  answerMode: string;
  isAiGenerated: boolean;
  safetyNotice: string;
  citations: AssistantCitation[];
}

export interface AssistantHistoryEntry {
  conversationId: number;
  updatedAt: string;
  lastQuestion?: string | null;
  lastAnswer?: string | null;
  lastStatus?: string | null;
}

export interface Preparation {
  id: number;
  name: string;
  dosageForm?: string | null;
  manufacturer?: string | null;
  registrationNumber?: string | null;
  registrationCountry?: string | null;
  description?: string | null;
  reviewStatus: ReviewStatus;
  source: Source;
}

export interface PreparationIngredient {
  id: number;
  ingredientName?: string | null;
  quantity?: string | null;
  plant?: Plant | null;
  compound?: CompoundSummary | null;
  reviewStatus: ReviewStatus;
  source: Source;
}

export interface FavoriteItem {
  id: number;
  type: 'PLANT' | 'COMPOUND' | 'PREPARATION';
  targetId: number;
  title: string;
  subtitle?: string | null;
  route: string;
  createdAt: string;
}

export interface AddFavoriteRequest {
  plantId?: number | null;
  compoundId?: number | null;
  preparationId?: number | null;
}
