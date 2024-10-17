package org.backrow.solt.config;

import org.backrow.solt.domain.board.Board;
import org.backrow.solt.domain.board.Reply;
import org.backrow.solt.domain.plan.Route;
import org.backrow.solt.domain.plan.TransportationType;
import org.backrow.solt.dto.plan.RouteDTO;
import org.backrow.solt.dto.reply.ReplyDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.typeMap(Reply.class, ReplyDTO.class)
                .addMappings(mapper -> mapper.using(ctx -> {
                    Board board = (Board) ctx.getSource();
                    if (board == null) return null;
                    return board.getBoardId();
                }).map(Reply::getBoard, ReplyDTO::setBoardId));

        modelMapper.typeMap(RouteDTO.class, Route.class)
                .addMappings(mapper -> {
                    mapper.map(RouteDTO::getRouteId, Route::setRouteId);
                    mapper.using(ctx -> {
                        Integer transportationId = (Integer) ctx.getSource();
                        if (transportationId == null) return null;

                        return TransportationType.builder()
                                .id(transportationId)
                                .build();
                    }).map(RouteDTO::getTransportationId, Route::setTransportationType);
                });

        return modelMapper;
    }
}
