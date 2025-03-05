package com.computerapplicationtechnologycnus.sakura_anime.controller;

import com.computerapplicationtechnologycnus.sakura_anime.common.ResultMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/privacy")
@Schema(description = "用户请求隐私相关处理方法API")
public class PrivacyController {
    // 检测到标头后的宣言
    String SEC_OR_DNT_DETECTED=
            "我们检测到您启用了隐私保护选项（Sec-GPC 或 DNT）。\n"+
            "我们尊重用户的隐私权，然而请注意：\n"+
            "根据当前法律框架，目前Sec-GPC 和 DNT 请求头并未被所有司法管辖区的法律明确认可为具有强制效力的隐私保护要求。\n"+
            "尽管我们检测到这些请求头，但我们可能不会完全遵循其表达的偏好。我们的数据处理行为将严格遵循我们的隐私政策以及适用的法律法规。"+
            "我们致力于保护用户的隐私，并将持续关注相关法律法规的变化。如有任何疑问，请联系我们。";
    // 没有检测到标头后的宣言
    String NO_SEC_OR_DNT_DETECTED="我们未检测到有效的 Sec-GPC 或 DNT 标头，您的隐私保护政策将遵从通用设定。";

    @Operation(description = "响应Sec-GPC和DNT表头检测")
    @GetMapping("/check")
    public ResultMessage<String> checkMyPrivacySettings(HttpServletRequest request){
        try{
            Boolean requestSecGpc = "1".equals(request.getHeader("Sec-GPC"));
            Boolean requestDoNotTrack = "1".equals(request.getHeader("DNT"));
            if(requestDoNotTrack || requestSecGpc){
                return ResultMessage.message(true,SEC_OR_DNT_DETECTED);
            }else{
                return ResultMessage.message(true,NO_SEC_OR_DNT_DETECTED);
            }
        }catch (Exception e){
            return ResultMessage.message(false,"检测失败！",e.getMessage());
        }
    }
}
