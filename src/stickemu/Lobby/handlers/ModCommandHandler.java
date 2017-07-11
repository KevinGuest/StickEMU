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
import stickemu.Types.StickClient;
import stickemu.Types.StickClientRegistry;
import stickemu.Types.StickRoomRegistry;
import stickemu.Types.StickRoom;
import stickemu.Tools.DatabaseTools;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import stickemu.Main;

/**
 *
 * @author Simon
 */
public class ModCommandHandler {
        public static void ProcessModCommand(StickClient client, String ModCommand)
        {
            if(!client.getModStatus()) //  <=== two bloody important lines of code there :P
                return;

            String[] ModCommandParsed = parseArgs(ModCommand);

            if(ModCommandParsed[0].equalsIgnoreCase("::ban"))
            {
                if(ModCommandParsed.length == 2)
                {
                    if(banPlayer(ModCommandParsed[1], client) > 0)
                        disconnectPlayer(ModCommandParsed[1]);
                    }
                else
                {
                    client.writeMessage("Usage: ::ban <username>");
                }
                return;
            }

            else if(ModCommandParsed[0].equalsIgnoreCase("::mute"))
            {
                if(ModCommandParsed.length == 2)
                {
                    StickClient SC = Main.getLobbyServer().getClientRegistry().getClientfromName(ModCommandParsed[1]);
                    if(SC != null)
                    {
                        SC.setMuteStatus(true); 
                        client.writeMessage("User " + ModCommandParsed[1] + " successfully muted.");
                    }
                    else
                    {
                        client.writeMessage("User " + ModCommandParsed[1] + " was not found.");
                    }
                }
                else
                {
                    client.writeMessage("Usage: ::mute <username>");
                }
                return;
            }

            else if(ModCommandParsed[0].equalsIgnoreCase("::unmute"))
            {
                if(ModCommandParsed.length == 2)
                {
                    StickClient SC = Main.getLobbyServer().getClientRegistry().getClientfromName(ModCommandParsed[1]);
                    if(SC != null)
                    {
                        SC.setMuteStatus(false);
                        client.writeMessage("User " + ModCommandParsed[1] + " successfully unmuted.");
                    }
                    else
                    {
                        client.writeMessage("User " + ModCommandParsed[1] + " was not found.");
                    }
                }
                else
                {
                    client.writeMessage("Usage: ::unmute <username>");
                }
                return;
            }

            else if(ModCommandParsed[0].equalsIgnoreCase("::deleteroom"))
            {
                if(ModCommandParsed.length >= 2)
                {
                    StickRoom Room = Main.getLobbyServer().getRoomRegistry().GetRoomFromName(ModCommand.substring(13).replaceAll("\0", ""));
                    if(Room != null)
                    {
                        Room.killRoom();
                    }
                    else
                    {
                        client.writeMessage("Room " + ModCommand.replaceAll("\0", "").substring(13) + " was not found.");
                    }
                }
                return;
            }

            else if(ModCommandParsed[0].equalsIgnoreCase("::disconnect"))
            {
                if(ModCommandParsed.length == 2)
                {
                    /*(StickClient SC = Main.getLobbyServer().getClientRegistry().getClientfromName(ModCommandParsed[1]);
                    if(SC != null)
                    {
                        SC.getIoSession().close(false); //the deregisterclient stuff will take care of this so we don't have to
                    }*/
                    if(!disconnectPlayer(ModCommandParsed[1]))
                    {
                        client.writeMessage("User " + ModCommandParsed[1] + " was not found.");
                    }
                }
                else
                {
                    client.writeMessage("Usage: ::disconnect <username>");
                }
                return;
            }

            else if(ModCommandParsed[0].equalsIgnoreCase("::ipban"))
            {
                StickClient SC = Main.getLobbyServer().getClientRegistry().getClientfromName(ModCommandParsed[1]);
                        if(SC == null)
                        {
                            client.writeMessage("Error IP Banning: User " + ModCommandParsed[1] + " does not exist or is not online.");
                            return;
                        }
                String IP = SC.getIoSession().getRemoteAddress().toString().substring(1).split(":")[0];
                System.out.println("IP address for user " + ModCommandParsed[1] + "is: " + IP + ".");
                synchronized (DatabaseTools.lock)
                {
                    try
                    {
                        PreparedStatement ps = DatabaseTools.getDbConnection().prepareStatement("INSERT INTO `ipbans` (`ip`, `playername`, `mod_responsible`) VALUES (?, ?, ?)");
                        ps.setString(1, IP);
                        ps.setString(2, ModCommandParsed[1]);
                        ps.setString(3, client.getName());
                        ps.executeUpdate();
                        SC.getIoSession().close(false);
                    } catch(SQLException e)
                    {
                        System.out.println("Exception whilst inserting IP ban: " + e.toString());
                    }
                }
                return;
            }

            else if(ModCommandParsed[0].equalsIgnoreCase("::announce"))
            {
                if(ModCommand.length() >10)
                    Main.getLobbyServer().BroadcastAnnouncement(ModCommand.substring(11).replaceAll("\0", ""));
                return;
            }

            else if(ModCommandParsed[0].equalsIgnoreCase("::getAllPlayers"))
            {
                StringBuilder Result = new StringBuilder();
                Result.append("User list: ");
                for(StickClient SC : Main.getLobbyServer().getClientRegistry().getAllClients())
                {
                    if (SC.getName() != null)
                        Result.append(" " + SC.getName() + ",");
                }
                client.writeMessage(Result.toString());
                return;
            }

            else if(ModCommandParsed[0].equalsIgnoreCase("::resetgametime") && client.getRoom() != null)
            {
                client.getRoom().setRoundTime(300);
                client.writeMessage("Round time successfully reset.");
            }

            //ban, mute, deleteroom, disconnect, ipban, announce

        }

        private static String[] parseArgs(String toParse)
        {
            return toParse.split(" ");
        }

        public static int banPlayer(String playerName, StickClient client)
        {
            PreparedStatement ps = null;
            int banResult = -1;
            synchronized (DatabaseTools.lock)
            {
                    try
                    {
                        ps = DatabaseTools.getDbConnection().prepareStatement("UPDATE `users` set `ban` = '1' where `username` = ?");
                        ps.setString(1, playerName);
                        banResult = ps.executeUpdate();
                    } catch (SQLException e)
                    {
                        System.out.println("Exception during ban command: " + e.toString());
                    }

                    if(banResult == -1)
                    {
                        client.writeMessage("There was an error banning " + playerName + ".");
                    }
                    else if(banResult == 0)
                    {
                        client.writeMessage("User " + playerName + " does not exist.");
                    }
                    else if(banResult > 1)
                    {
                        client.writeMessage("User " + playerName + " was banned successfully.");
                    }
            }
            return banResult;
        }

        private static Boolean disconnectPlayer(String playerName) //returns true if player found and dc'ed
        {
            StickClient SC = null;
            int count = 0;
            do
            {
            SC = Main.getLobbyServer().getClientRegistry().getClientfromName(playerName);
                if(SC != null)
                {
                    SC.getIoSession().close(false); //the deregisterclient stuff will take care of this so we don't have to
                }
            count++;
            } while (SC != null);
            return(count > 0);
        }


}
