export default function StatusBadge({ status }) {
  const value = status || "UNKNOWN";
  return <span className={`status-badge ${value.toLowerCase()}`}>{value}</span>;
}