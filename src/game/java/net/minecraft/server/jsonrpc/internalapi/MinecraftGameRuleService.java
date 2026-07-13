package net.minecraft.server.jsonrpc.internalapi;

import java.util.stream.Stream;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.world.level.gamerules.GameRule;

public interface MinecraftGameRuleService {
    <T> GameRulesService.GameRuleUpdate<T> updateGameRule(GameRulesService.GameRuleUpdate<T> p_461195_, ClientInfo p_443494_);

    <T> T getRuleValue(GameRule<T> p_460739_);

    <T> GameRulesService.GameRuleUpdate<T> getTypedRule(GameRule<T> p_461234_, T p_461216_);

    Stream<GameRule<?>> getAvailableGameRules();
}
