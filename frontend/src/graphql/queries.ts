import { gql } from '@apollo/client';

export const GET_ACTIVE_ALERTS = gql`
  query GetActiveAlerts {
    activeAlerts {
      id
      title
      description
      severity
      status
      createdAt
      location {
        lat
        lng
        zone
        affectedBuildings
      }
      deliveryStats {
        totalRecipients
        delivered
        failed
        avgLatencyMs
        byChannel {
          channel
          sent
          delivered
          failed
          latencyMs
        }
      }
    }
  }
`;

export const GET_ALERT = gql`
  query GetAlert($id: ID!) {
    alert(id: $id) {
      id
      title
      description
      severity
      status
      createdAt
      location {
        lat
        lng
        zone
        affectedBuildings
      }
      deliveryStats {
        totalRecipients
        delivered
        failed
        avgLatencyMs
        byChannel {
          channel
          sent
          delivered
          failed
          latencyMs
        }
      }
    }
  }
`;

export const GET_ALERT_HISTORY = gql`
  query GetAlertHistory($zone: String, $severity: Severity, $limit: Int) {
    alertHistory(zone: $zone, severity: $severity, limit: $limit) {
      id
      title
      severity
      status
      createdAt
      location {
        lat
        lng
        zone
      }
    }
  }
`;

export const CREATE_ALERT = gql`
  mutation CreateAlert($input: CreateAlertInput!) {
    createAlert(input: $input) {
      id
      title
      severity
      status
      createdAt
      location {
        lat
        lng
        zone
      }
    }
  }
`;

export const ACKNOWLEDGE_ALERT = gql`
  mutation AcknowledgeAlert($id: ID!) {
    acknowledgeAlert(id: $id) {
      id
      status
    }
  }
`;

export const RESOLVE_ALERT = gql`
  mutation ResolveAlert($id: ID!) {
    resolveAlert(id: $id) {
      id
      status
    }
  }
`;

export const ALERT_CREATED_SUBSCRIPTION = gql`
  subscription OnAlertCreated {
    alertCreated {
      id
      title
      description
      severity
      status
      createdAt
      location {
        lat
        lng
        zone
        affectedBuildings
      }
    }
  }
`;

export const DELIVERY_UPDATE_SUBSCRIPTION = gql`
  subscription OnDeliveryUpdate($alertId: ID!) {
    deliveryUpdate(alertId: $alertId) {
      totalRecipients
      delivered
      failed
      avgLatencyMs
      byChannel {
        channel
        sent
        delivered
        failed
        latencyMs
      }
    }
  }
`;
