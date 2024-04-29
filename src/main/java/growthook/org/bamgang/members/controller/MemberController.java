package growthook.org.bamgang.members.controller;

import growthook.org.bamgang.members.domain.FinishedWalk;
import growthook.org.bamgang.members.domain.Member;
import growthook.org.bamgang.members.domain.PickedWalk;
import growthook.org.bamgang.members.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/members")
public class MemberController {

    @Autowired
    private final MemberService memberService;


    @Autowired
    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping()
    public Member getUserInfo(@RequestBody Member info){
        int id = info.getUserId();
        Member member = memberService.findById(id);
        member.setUserId(null);
        return member;
    }

    @GetMapping("/list3")
    public List<FinishedWalk> getUserInfo3(@RequestBody Member member){
        int id = member.getUserId();
        List<FinishedWalk> walks = memberService.findFinishedWalkById(id);
        for(FinishedWalk walk : walks){
            walk.setUserId(null);
            walk.setFinishedId(null);
        }
        return walks;
    }

    @GetMapping("/list4")
    public List<PickedWalk> getUserInfo4(@RequestBody Member member){
        int id = member.getUserId();
        List<PickedWalk> walks = memberService.findPickedWalkById(id);
        for(PickedWalk walk : walks){
            walk.setUserId(null);
        }
        return walks;
    }

    @PostMapping("/list4")
    public void postUserInfo4(@RequestBody PickedWalk pickedWalk){
        int userId = pickedWalk.getUserId();
        int trailId = pickedWalk.getTrailId();
        memberService.savePickedWalk(userId,trailId);
    }

    @DeleteMapping("/list4")
    public void deleteUserInfo4(@RequestBody PickedWalk pickedWalk){
        int userId = pickedWalk.getUserId();
        int trailId = pickedWalk.getTrailId();
        memberService.deletePickedWalk(userId,trailId);
    }

}
