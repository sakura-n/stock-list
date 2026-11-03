package com.example.stocklist.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 文档目录信息配置
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI stockListOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("囤货清单 API 文档")
                        .version("1.0")
                        .description("库存囤货清单小程序后端接口："
                                + "库存物品 CRUD（/api/items）、"
                                + "品类下拉（/api/categories）、"
                                + "待购买清单（/api/purchase-list，含库存不足与即将过期两组）、"
                                + "全局阈值设置（/api/settings）")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@example.com")));
    }
}
