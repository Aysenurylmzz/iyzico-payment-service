package com.aysenur.payment_service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.aysenur.payment_service.controller.PaymentController;
import com.aysenur.payment_service.service.PaymentService;
import com.aysenur.payment_service.service.WebhookService;

class PaymentControllerTest {

    private PaymentService paymentService; //paymentservisi mock olarak kullanıcak
    private MockMvc mockMvc; //http isteklerini test etmek için mockMvc nesnesi

    @BeforeEach  //her testten önce çalışıyor 
    void setUp() {
        // gerçek servis yerine sahte nesne oluşturuyor
        paymentService = mock(PaymentService.class);
        WebhookService webhookService = mock(WebhookService.class);
         
        //controller oluşturuyor 
        PaymentController controller =
                new PaymentController(
                        paymentService,
                        webhookService
                );

        //controller test ortamına hazırlanıyor
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

     //Callback isteğinin doğru sayfaya yönlendirdiği test edilir
    @Test
    void callbackShouldRedirectToResultPage() throws Exception {

        String token = "test-token";
        String conversationId = "conversation-123";

        //processCallback metodu çağırıldığında conversationId döndürmesi sağlanır.
        when(paymentService.processCallback(token))
                .thenReturn(conversationId);

        //Callback endpointine POST isteği gönderilir
        mockMvc.perform(
                        post("/api/payments/callback")
                                .param("token", token)
                )
                //HTTP 302 dönmesi beklenir
                .andExpect(status().isFound())
                //yönlendirme adresinin doğru olduğu doğrulanır 
                .andExpect(
                        header().string(
                                "Location",
                                "http://localhost:5173/payment/result?ref="
                                        + conversationId
                        )
                );
    }
}
