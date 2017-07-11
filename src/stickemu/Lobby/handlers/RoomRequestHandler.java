/*
 *     THIS FILE AND PROJECT IS SUPPLIED FOR EDUCATIONAL PURPOSES ONLY.
 *
 *     This program is free software; you can redistribute it
 *     and/or modify it under the terms of the GNU General
 *     Public License as published by the Free Software
 *     Foundation; either version 2 of the License, or (at your
 *     option) any later version.
 *
 *     This program is distributed in the hope that it will be
 *     useful, but WITHOUT ANY WARRANTY; without even the
 *     implied warranty of MERCHANTABILITY or FITNESS FOR A
 *     PARTICULAR PURPOSE. See the GNU General Public License
 *     for more details.
 *
 *     You should have received a copy of the GNU General
 *     Public License along with this program; if not, write to
 *     the Free Software Foundation, Inc., 59 Temple Place,
 */
package stickemu.Lobby.handlers;
import stickemu.Main;
import stickemu.Types.StickClient;
import stickemu.Tools.StickPacketMaker;
/**
 *
 * @author Simon
 */
public class RoomRequestHandler {
public static void handlePacket(StickClient client)
        {
            if (!client.getLobbyStatus())
            {
                client.getRoom().GetCR().deregisterClient(client);
                client.setLobbyStatus(true);
                client.setRoom(null);
                Main.getLobbyServer().BroadcastPacket(StickPacketMaker.HeresYourUID(client));
                client.write(StickPacketMaker.getUserList(Main.getLobbyServer().getClientRegistry(), client.getUID(), true, client));
            }
            client.write(StickPacketMaker.getRoomList());
        }
}
