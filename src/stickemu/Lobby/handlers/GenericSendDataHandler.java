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
import stickemu.Tools.StickPacketMaker;
import stickemu.Types.StickClient;
/**
 *
 * @author Simon
 */
public class GenericSendDataHandler {
public static void HandlePacket(StickClient client, String Packet)
        {

            if (Packet.length() < 5) { return; }
            String ToUID = Packet.substring(2, 5);

            // do we know you?
            if (client.getName() == null)
            {

                    client.setName(Packet.substring(6, 26).replaceAll("0", ""));
                    client.setColour(Packet.substring(26, 35));
                    try
                    {
                        client.setKills(Integer.parseInt(Packet.substring(35, (Packet.length() - 36)+35)));
                    }
                    catch (Exception e)
                    {
                        client.setKills(0);
                    }
            }
            Main.getLobbyServer().sendToUID(ToUID, StickPacketMaker.GenericSendPacket(client.getUID(), Packet.substring(5)));

        }

    }
