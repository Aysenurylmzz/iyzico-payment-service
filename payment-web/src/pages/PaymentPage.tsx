import { useState } from "react";
import { initializeCheckoutForm } from "../api/paymentApi";
import IyzicoCheckoutForm from "../components/IyzicoCheckoutForm";

function PaymentPage() {
  const [buyerName, setBuyerName] = useState("");
  const [buyerSurname, setBuyerSurname] = useState("");
  const [buyerEmail, setBuyerEmail] = useState("");
  const [price, setPrice] = useState("100");
  const [checkoutFormContent, setCheckoutFormContent] =
    useState<string | null>(null);

  const handleSubmit = async (
    event: React.FormEvent
  ) => {
    event.preventDefault();

    const response = await initializeCheckoutForm({
      price: Number(price),
      paidPrice: Number(price),
      currency: "TRY",

      buyerName,
      buyerSurname,
      buyerEmail,

      buyerPhone: "5551112233",
      buyerIdentityNumber: "11111111111",

      address: "Test Mahallesi",
      city: "İstanbul",
      country: "Turkey",
      zipCode: "34000"
    });

    if (response.checkoutFormContent) {
      setCheckoutFormContent(response.checkoutFormContent);
    } else {
      alert(response.errorMessage);
    }
  };

  return (
    <div>
      <h1>İyzico Payment Service</h1>

      <h2>Ödeme Bilgileri</h2>

      <form onSubmit={handleSubmit}>
        <div>
          <label>Ad</label>
          <br />
          <input
            type="text"
            placeholder="Adınızı giriniz"
            value={buyerName}
            onChange={(event) => setBuyerName(event.target.value)}
          />
        </div>

        <br />

        <div>
          <label>Soyad</label>
          <br />
          <input
            type="text"
            placeholder="Soyadınızı giriniz"
            value={buyerSurname}
            onChange={(event) => setBuyerSurname(event.target.value)}
          />
        </div>

        <br />

        <div>
          <label>E-posta</label>
          <br />
          <input
            type="email"
            placeholder="E-posta adresinizi giriniz"
            value={buyerEmail}
            onChange={(event) => setBuyerEmail(event.target.value)}
          />
        </div>

        <br />

        <div>
          <label>Tutar</label>
          <br />
          <input
            type="number"
            value={price}
            onChange={(event) => setPrice(event.target.value)}
          />
        </div>

        <br />

        <button type="submit">
          Ödeme Yap
        </button>
      </form>

      {checkoutFormContent && (
        <IyzicoCheckoutForm content={checkoutFormContent} />
      )}
    </div>
  );
}

export default PaymentPage;