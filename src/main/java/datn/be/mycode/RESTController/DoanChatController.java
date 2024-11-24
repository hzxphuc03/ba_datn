package datn.be.mycode.RESTController;

import datn.be.mycode.request.NhanTin.NhanVien.DoanChat.AddDoanChat;
import datn.be.mycode.request.NhanTin.NhanVien.DoanChat.GetByHopThoai;
import datn.be.mycode.service.DoanChatNhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doan-chat")
public class DoanChatController {

    @Autowired
    private DoanChatNhanVienService doanChatNhanVienService;

    @GetMapping("/get-doan-chat-by-hop-thoai")
    public ResponseEntity<?> getDoanChat(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "idHopThoai",required = false) Long idHopThoai
    ){
        GetByHopThoai request = new GetByHopThoai(page,pageSize,idHopThoai);
        return ResponseEntity.ok(doanChatNhanVienService.getDoanChatNhanVienByHopThoai(request));
    }

    @PostMapping("/add-doan-chat")
    public ResponseEntity<?> addDoanChat(@RequestBody AddDoanChat request){

        return ResponseEntity.ok(doanChatNhanVienService.addDoanChatNhanVien(request));
    }
}
