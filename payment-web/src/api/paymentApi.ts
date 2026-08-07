import axios from "axios";

export interface CheckoutFormRequest {
  price: number;
  paidPrice: number;
  currency: string;
  buyerName: string;
  buyerSurname: string;
  buyerEmail: string;
  buyerPhone: string;
  buyerIdentityNumber: string;
  address: string;
  city: string;
  country: string;
  zipCode: string;
}

export interface CheckoutFormResponse {
  status: string;
  paymentPageUrl: string | null;
  checkoutFormContent: string | null;
  token: string | null;
  errorMessage: string | null;
}

const paymentApi = axios.create({
  baseURL: "http://localhost:8080/api/payments",
  headers: {
    "X-Api-Key": import.meta.env.VITE_INTERNAL_API_KEY,
  },
});

export async function initializeCheckoutForm(
  request: CheckoutFormRequest
): Promise<CheckoutFormResponse> {
  const response = await paymentApi.post<CheckoutFormResponse>(
    "/checkout-form",
    request
  );

  return response.data;
}

export default paymentApi;
