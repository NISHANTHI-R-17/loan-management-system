export default function ErrorMessage({ message, onClose }) {
  if (!message) return null;

  return (
    <div className="alert error-alert">
      <span>{message}</span>
      {onClose && (
        <button type="button" onClick={onClose}>
          ×
        </button>
      )}
    </div>
  );
}