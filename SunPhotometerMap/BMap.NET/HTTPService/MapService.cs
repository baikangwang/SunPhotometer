using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Drawing;
using System.IO;

namespace BMap.NET.HTTPService
{
    /// <summary>
    /// 提供地图相关服务
    /// </summary>
    public class MapService : ServiceBase
    {
        private static string _road_url = "http://online{0}.map.bdimg.com/onlinelabel/";  //地图切片URL
        private static string _sate_url = "http://shangetu{0}.map.bdimg.com/it/";  //卫星图切片URL
        private static string _tile_url = "http://api{0}.map.bdimg.com/customimage/";  //卫星图切片URL with custom style

        /* http://online9.map.bdimg.com/onlinelabel/?qt=tile&x=796&y=287&z=12&styles=pl */
        /* http://shangetu9.map.bdimg.com/it/u=x=796;y=287;z=13;v=009;type=sate&fm=46 */
        /* http://api1.map.bdimg.com/customimage/tile?&x=3165&y=1177&z=14&customid=grayscale*/

        /// <summary>
        /// 下载地图瓦片
        /// </summary>
        /// <param name="x">瓦片方块横坐标</param>
        /// <param name="y">瓦片方块纵坐标</param>
        /// <param name="zoom">当前地图缩放级别（1-18）</param>
        /// <param name="map_mode">地图模式</param>
        /// <param name="load_mode">加载瓦片方式</param>
        /// <returns></returns>
        public Bitmap LoadMapTile(int x, int y, int zoom, MapMode map_mode, LoadMapMode load_mode, MapStyle mapStyle = MapStyle.normal)
        {
            if (load_mode == LoadMapMode.Server)  //直接从服务器下载图片
            {
                return TileFromServer(zoom, x, y, map_mode, mapStyle);
            }
            else if (load_mode == LoadMapMode.Cache)  //从本地缓存中下载图片
            {
                return TileFromCache(zoom, x, y, map_mode, mapStyle);
            }
            else if (load_mode == LoadMapMode.CacheServer)  //先从本地缓存中找，如果没有则从服务器上下载
            {
                Bitmap bitmap = TileFromCache(zoom, x, y, map_mode, mapStyle);
                if (bitmap == null)
                {
                    bitmap = TileFromServer(zoom, x, y, map_mode, mapStyle);
                }
                return bitmap;
            }
            else
            {
                return null;
            }
        }

        /// <summary>
        /// 清空瓦片缓存
        /// </summary>
        public void ClearTileCache()
        {
            try
            {
                string cache_path = Properties.BMap.Default.MapCachePath;
                DirectoryInfo dir = new DirectoryInfo(cache_path);
                foreach (DirectoryInfo d in dir.GetDirectories())
                {
                    if (d.Name == MapMode.Normal.ToString() || d.Name == MapMode.RoadNet.ToString() || d.Name == MapMode.Satellite.ToString())
                    {
                        d.Delete(true);
                    }
                }
            }
            catch
            {
            }
        }

        /// <summary>
        /// 从缓存中加载瓦片
        /// </summary>
        /// <param name="zoom"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <param name="map_mode"></param>
        /// <returns></returns>
        private Bitmap TileFromCache(int zoom, int x, int y, MapMode map_mode, MapStyle mapStyle = MapStyle.normal)
        {
            try
            {
                string cache_path = Properties.BMap.Default.MapCachePath;
                string map_tile_dir = map_mode == MapMode.Normal
                    ? Path.Combine(cache_path, map_mode.ToString(), mapStyle.ToString())
                    : Path.Combine(cache_path, map_mode.ToString());
                if (Directory.Exists(map_tile_dir))
                {
                    string cache_name = Path.Combine(map_tile_dir, string.Format("{0}_{1}_{2}.bmp", zoom, x, y));
                    if (File.Exists(cache_name))
                    {
                        return new Bitmap(cache_name);
                    }
                    else
                    {
                        return null;
                    }
                }
                else
                {
                    return null;
                }
            }
            catch
            {
                return null;
            }
        }

        /// <summary>
        /// 从服务器上加载瓦片
        /// </summary>
        /// <param name="zoom"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <param name="map_mode"></param>
        /// <returns></returns>
        private Bitmap TileFromServer(int zoom, int x, int y, MapMode map_mode, MapStyle mapStyle = MapStyle.normal)
        {
            try
            {
                Random r = new Random(DateTime.Now.Millisecond);
                int server_index = -1;
                string url = "";
                if (map_mode == MapMode.Normal) //地图
                {
                    // server_index = r.Next(0, 10);  //随即产生0~9之间的整数
                    // url = String.Format(_road_url, server_index) + "?qt=tile&x=" + x + "&y=" + y + "&z=" + zoom + "&styles=pl";
                    server_index = r.Next(0, 3);  //随即产生0~2之间的整数
                    url = String.Format(_tile_url, server_index) + "tile?&x=" + x + "&y=" + y + "&z=" + zoom + "&customid=" + mapStyle.ToString();
                }
                if (map_mode == MapMode.Satellite) //卫星图
                {
                    server_index = r.Next(0, 10);  //随即产生0~9之间的整数
                    url = String.Format(_sate_url, server_index) + "u=x=" + x + ";y=" + y + ";z=" + zoom + ";v=009;type=sate&fm=46";
                }
                if (map_mode == MapMode.RoadNet) //道路网
                {
                    server_index = r.Next(0, 10);  //随即产生0~9之间的整数
                    url = String.Format(_road_url, server_index) + "?qt=tile&x=" + x + "&y=" + y + "&z=" + zoom + "&styles=sl";
                }
                byte[] bytes = DownloadData(url);
                Bitmap bitmap = Image.FromStream(new MemoryStream(bytes)) as Bitmap;
                SaveTile2Cache(zoom, x, y, map_mode, bitmap, mapStyle);
                return bitmap;
            }
            catch
            {
                return null;
            }
        }

        /// <summary>
        /// 将从服务器上下载的瓦片保存到缓存
        /// </summary>
        /// <param name="zoom"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <param name="map_mode"></param>
        /// <param name="tile"></param>
        private void SaveTile2Cache(int zoom, int x, int y, MapMode map_mode, Bitmap tile, MapStyle mapStype = MapStyle.normal)
        {
            try
            {
                string cache_path = Properties.BMap.Default.MapCachePath;
                string file_path = Path.Combine(cache_path, map_mode.ToString());
                if (!Directory.Exists(file_path))
                {
                    Directory.CreateDirectory(file_path);
                }
                // only the map mode of normal has vary of style
                if (map_mode == MapMode.Normal)
                {
                    file_path = Path.Combine(file_path, mapStype.ToString());
                    if (!Directory.Exists(file_path))
                    {
                        Directory.CreateDirectory(file_path);
                    }
                }
                string file = Path.Combine(file_path, string.Format("{0}_{1}_{2}.bmp", zoom, x, y));
                tile.Save(file);
            }
            catch
            {
            }
        }
    }
}