import { HttpParams } from '@angular/common/http';

export interface CatalogQuery {
  query?: string;
  family?: string;
  plantId?: number;
  structureStatus?: string;
  page?: number;
  size?: number;
  sort?: string;
}

export function toHttpParams(values: object): HttpParams {
  let params = new HttpParams();
  for (const [key, value] of Object.entries(values as Record<string, string | number | undefined>)) {
    if (value !== undefined && String(value).trim() !== '') params = params.set(key, String(value));
  }
  return params;
}
