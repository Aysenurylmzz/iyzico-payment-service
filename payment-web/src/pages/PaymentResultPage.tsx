import { useEffect, useState } from "react";
import paymentApi from "../api/paymentApi";

interface PaymentResponse {
  id: number;
  paymentId: string | null;
  conversationId: string;
  price: number;
  paidPrice: number;
  currency: string;
  status: string;
  createdAt: string;
}

function PaymentResultPage() {
  const [payment, setPayment] = useState<PaymentResponse | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const conversationId = params.get("ref");

    if (!conversationId) {
      setError("Ödeme referansı bulunamadı.");
      return;
    }

    paymentApi
      .get<PaymentResponse>(`/${conversationId}`)
      .then((response) => {
        setPayment(response.data);
      })
      .catch(() => {
        setError("Ödeme bilgileri alınamadı.");
      });
  }, []);

  if (error) {
    return <h2>{error}</h2>;
  }

  if (!payment) {
    return <p>Ödeme sonucu yükleniyor...</p>;
  }

  return (
    <div>
      <h1>Ödeme Sonucu</h1>

      <p>Durum: {payment.status}</p>
      <p>Tutar: {payment.paidPrice} {payment.currency}</p>
      <p>İşlem Numarası: {payment.conversationId}</p>
      <p>İyzico Ödeme Numarası: {payment.paymentId}</p>
    </div>
  );
}

export default PaymentResultPage;
