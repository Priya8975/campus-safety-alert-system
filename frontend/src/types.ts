export type Severity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type AlertStatus = 'ACTIVE' | 'ACKNOWLEDGED' | 'RESOLVED';

export interface Location {
  lat: number;
  lng: number;
  zone: string | null;
  affectedBuildings: string[];
}

export interface ChannelStat {
  channel: string;
  sent: number;
  delivered: number;
  failed: number;
  latencyMs: number;
}

export interface DeliveryStats {
  totalRecipients: number;
  delivered: number;
  failed: number;
  avgLatencyMs: number;
  byChannel: ChannelStat[];
}

export interface Alert {
  id: string;
  title: string;
  description: string | null;
  severity: Severity;
  location: Location;
  status: AlertStatus;
  deliveryStats: DeliveryStats | null;
  createdAt: string;
}

export interface CreateAlertInput {
  title: string;
  description?: string;
  severity: Severity;
  lat: number;
  lng: number;
  campusZone?: string;
}
