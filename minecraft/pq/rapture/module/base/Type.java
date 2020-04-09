package pq.rapture.module.base;

import pq.rapture.protection.Protection;

public enum Type {

	EXPLOITS, MOVEMENT, PLAYER, WORLD, RENDER, COMBAT, CORE;
	
	public static String getText(Type type) {
		switch(type) {
		case EXPLOITS:
			return Protection.decrypt("59157D42EB03965DD9F1AB88239F7956");
		case MOVEMENT:
			return Protection.decrypt("13953B061C7E254E150402547B967031");
		case PLAYER:
			return Protection.decrypt("72DA809AE97C72DF07C47B692FE42AB7");
		case WORLD:
			return Protection.decrypt("76FFA7FB0C7168F713B796B7E0CCF7EC");
		case RENDER:
			return Protection.decrypt("0014641B441DD054CB9F4EA92805CB31");
		case COMBAT:
			return Protection.decrypt("A5D1FE7C081600FC27A714916F7E1842");
		default:
			break;
		}
		return "";
	}
}
