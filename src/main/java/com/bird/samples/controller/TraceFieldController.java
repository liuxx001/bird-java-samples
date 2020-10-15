package com.bird.samples.controller;

import com.bird.core.NameValue;
import com.bird.core.Result;
import com.bird.core.exception.UserFriendlyException;
import com.bird.core.trace.Traceable;
import com.bird.samples.model.TraceDemoDO;
import com.bird.samples.pojo.CrudDemoBO;
import com.bird.samples.service.TraceDemoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author liuxx
 * @since 2020/9/30
 */
@RestController
@RequestMapping("/trace/field")
public class TraceFieldController {

    private final TraceDemoService traceDemoService;

    public TraceFieldController(TraceDemoService traceDemoService) {
        this.traceDemoService = traceDemoService;
    }

    @GetMapping("/insert")
    public void insert() {
        TraceDemoDO traceDemo = new TraceDemoDO().setName("liuxx").setDescription("desc").setNum(1).setDate(new Date());
        traceDemoService.insert(traceDemo);
    }

    @GetMapping("/insertDto")
    public Result<String> insertDto() {
        CrudDemoBO demo = new CrudDemoBO().setName("liuxx").setDescription("desc").setNum(1).setDate(new Date());
        return Result.success("success",traceDemoService.insert(demo));
    }

    @Transactional(rollbackFor = Exception.class)
    @Traceable("test")
    @GetMapping("/update")
    public void update() {
        String updateValue = UUID.randomUUID().toString();

        TraceDemoDO traceDemo = traceDemoService.lambdaQuery().eq(TraceDemoDO::getName, "liuxx").last("limit 1").one();
        traceDemo.setName(updateValue).setDescription(updateValue).setNum(ThreadLocalRandom.current().nextInt(100)).setDate(new Date());
        traceDemoService.update(traceDemo);


//        String updateValue2 = UUID.randomUUID().toString();
//        traceDemo.setName(updateValue2).setDescription(updateValue2).setNum(ThreadLocalRandom.current().nextInt(100)).setDate(new Date());
//        traceDemoService.update(traceDemo);
    }

    @GetMapping("/lambadaUpdate")
    public void lambadaUpdate() {
        traceDemoService.lambdaUpdate().eq(TraceDemoDO::getName,"liuxx").set(TraceDemoDO::getName,"liuxx02").update();
    }

    @GetMapping("/update2")
    public void update2() {
        traceDemoService.updateName();
    }

    @GetMapping("/delete")
    public void delete() {
        TraceDemoDO traceDemo = traceDemoService.lambdaQuery().last("limit 1").one();
        traceDemoService.delete(traceDemo.getId());
    }

    @GetMapping("/rollBack")
    @Transactional(rollbackFor = Exception.class)
    public void rollBack() {
        TraceDemoDO traceDemo = traceDemoService.lambdaQuery().last("limit 1").one();
        traceDemoService.delete(traceDemo.getId());
        throw new UserFriendlyException("roll back");
    }

    @PostMapping("/request")
    public NameValue request(@RequestBody NameValue nameValue) {
        return nameValue;
    }
}
