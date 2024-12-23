package org.anonbin.controller;

import jakarta.servlet.http.HttpSession;
import org.anonbin.model.BinModel;
import org.anonbin.service.BinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class WebController {

    @Autowired
    private BinService binService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/create")
    public String createBin(@RequestParam String title,
                            @RequestParam String description,
                            @RequestParam(required = false) String password,
                            @RequestParam String expirationTime,
                            Model model) {

        // Converter expirationTime (String) para LocalDateTime
        LocalDateTime expiration = LocalDateTime.parse(expirationTime);

        // Criar o modelo da bin
        BinModel binModel = new BinModel();
        binModel.setTitle(title);
        binModel.setDescription(description);
        binModel.setPassword(password);
        binModel.setExpirationTime(expiration);

        // Usar o serviço para criar a bin
        BinModel createdBin = binService.createBin(binModel);

        // Adicionar o link gerado ao modelo
        String binLink = "/bin/" + createdBin.getSlug();
        model.addAttribute("binLink", binLink);

        return "index"; // Página que exibe o link ou uma mensagem de sucesso
    }

    @GetMapping("/bin/{slug}")
    public String viewBin(@PathVariable String slug, Model model, HttpSession session) {
        // Buscar a bin pelo slug
        BinModel bin = binService.getBinBySlug(slug);

        if (bin != null) {
            // Verificar se a bin tem uma senha válida, não nula e não vazia
            if (bin.getPassword() != null && !bin.getPassword().isEmpty()) {
                // Verificar se a senha foi validada na sessão
                Boolean isPasswordVerified = (Boolean) session.getAttribute("passwordVerified:" + slug);
                if (Boolean.TRUE.equals(isPasswordVerified)) {
                    // Formatar a data de expiração da bin, caso exista
                    if (bin.getExpirationTime() != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
                        String formattedExpirationTime = bin.getExpirationTime().format(formatter);
                        model.addAttribute("formattedExpirationTime", formattedExpirationTime);
                    }
                    model.addAttribute("bin", bin);
                    return "binView"; // Página para exibir a bin
                } else {
                    // Redireciona para a página de verificação de senha
                    model.addAttribute("slug", slug); // Passa o slug para a próxima página
                    return "passwordPage"; // Página para o usuário inserir a senha
                }
            } else {
                // Se a bin não tem senha
                if (bin.getExpirationTime() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
                    String formattedExpirationTime = bin.getExpirationTime().format(formatter);
                    model.addAttribute("formattedExpirationTime", formattedExpirationTime);
                }
                model.addAttribute("bin", bin);
                return "binView"; // Página para exibir a bin
            }
        } else {
            model.addAttribute("error", "Bin not found");
            return "errorPage"; // Página de erro caso a bin não seja encontrada
        }
    }


    @PostMapping("/verifyPassword")
    public String verifyPassword(@RequestParam String password, @RequestParam String slug, RedirectAttributes redirectAttributes, HttpSession session) {
        // Buscar a bin pelo slug
        BinModel bin = binService.getBinBySlug(slug);

        if (bin != null && bin.getPassword() != null && bin.getPassword().equals(password)) {
            // Armazena no HttpSession que a senha foi verificada para este slug
            session.setAttribute("passwordVerified:" + slug, true);

            // Redireciona para a URL da bin
            return "redirect:/bin/" + slug;
        } else {
            // Se a senha estiver incorreta, redireciona de volta para a página de senha com erro
            redirectAttributes.addFlashAttribute("error", "Invalid password!");
            return "redirect:/bin/" + slug;
        }
    }

}
