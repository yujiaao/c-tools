Add-Type -AssemblyName System.Drawing
$bmp = New-Object System.Drawing.Bitmap 256,256
$gfx = [System.Drawing.Graphics]::FromImage($bmp)
$bgColor = [System.Drawing.Color]::FromArgb(0x16,0x3d,0x5a)
$accent = [System.Drawing.Color]::FromArgb(0xff,0xc1,0x49)
$gfx.Clear($bgColor)
$font = New-Object System.Drawing.Font ('Segoe UI', 96, [System.Drawing.FontStyle]::Bold)
$format = New-Object System.Drawing.StringFormat
$format.Alignment = [System.Drawing.StringAlignment]::Center
$format.LineAlignment = [System.Drawing.StringAlignment]::Center
$textRect = New-Object System.Drawing.RectangleF 0,0,256,256
$brush = New-Object System.Drawing.SolidBrush ($accent)
$penColor = [System.Drawing.Color]::FromArgb(0xff,0x8f,0x39)
$pen = New-Object System.Drawing.Pen $penColor,6
$gfx.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAlias
$gfx.DrawString('TG',$font,$brush,$textRect,$format)
$gfx.DrawEllipse($pen,18,18,220,220)
$bmp.Save('images/icon.png',[System.Drawing.Imaging.ImageFormat]::Png)
$pen.Dispose()
$brush.Dispose()
$font.Dispose()
$gfx.Dispose()
$bmp.Dispose()
