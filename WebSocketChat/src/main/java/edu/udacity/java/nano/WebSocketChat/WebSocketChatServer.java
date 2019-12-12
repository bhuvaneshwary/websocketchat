package edu.udacity.java.nano.WebSocketChat;

import javax.servlet.http.HttpSession;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebSocketChatServer {
	
	//Root route displays the login page
	@RequestMapping("/")
	public String home() {
		return "login";
	}
	
	// Route handles server side validation for empty username, redirects to /chat
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public String login(@RequestParam(value="username") String username, HttpSession session,RedirectAttributes redirectAttributes) {
		if(username != null && !username.isEmpty()) {
			session.setAttribute("username", username);
			return "redirect:/chat";
		}
		redirectAttributes.addFlashAttribute("error","Please enter a username.");
		return "redirect:/";
	}
	
	//Route displays the chat form upon successful login
	@RequestMapping("/chat")
	public String chat(HttpSession session, Model model,RedirectAttributes redirectAttributes) {
		
		if(session.getAttribute("username")!=null) {
			String username = (String) session.getAttribute("username");
			model.addAttribute("username",username);
			return "chat";
		}
		
		redirectAttributes.addFlashAttribute("error","Please login first.");
		return "redirect:/";
				
	}
	
	// User join
	@MessageMapping("/chat.register")
	@SendTo("/topic/public")
	public ChatMessage register(@Payload ChatMessage chatMessage,SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		ChatMessage.addUser(chatMessage.getSender());
		return chatMessage;
	}
	
	// User sends chat message
	@MessageMapping("/chat.send")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
		return chatMessage;
	}
	
	// User leaves the chatroom
	@MessageMapping("/chat.leave")
	@SendTo("/topic/public")
	public ChatMessage leave(@Payload ChatMessage chatMessage,SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.removeHeader("username");
		chatMessage.setContent(chatMessage.getSender() + " has left the chat.");
		ChatMessage.removeUser(chatMessage.getSender());
		return chatMessage;
	}
	
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
}
