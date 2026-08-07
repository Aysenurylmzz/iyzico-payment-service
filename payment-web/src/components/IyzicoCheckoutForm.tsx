import { useEffect, useRef } from "react";

type Props = {
  content: string;
};

function IyzicoCheckoutForm({ content }: Props) {
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!containerRef.current) return;

    containerRef.current.innerHTML = "";

    const mount = document.createElement("div");
    mount.id = "iyzipay-checkout-form";
    mount.className = "responsive";

    containerRef.current.appendChild(mount);

    const temp = document.createElement("div");
    temp.innerHTML = content;

    const scripts = temp.querySelectorAll("script");

    scripts.forEach((oldScript) => {
      const newScript = document.createElement("script");

      Array.from(oldScript.attributes).forEach((attribute) => {
        newScript.setAttribute(
          attribute.name,
          attribute.value
        );
      });

      newScript.text = oldScript.text;

      document.body.appendChild(newScript);
    });

  }, [content]);

  return <div ref={containerRef} />;
}

export default IyzicoCheckoutForm;
