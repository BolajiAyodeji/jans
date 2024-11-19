package com.example.fido2.ui.common.customComposableViews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.fido2.ui.theme.AppTheme
import com.example.fido2.ui.theme.LightColors

@Composable
fun NormalButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .height(AppTheme.dimens.normalButtonHeight),
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
        onClick = onClick
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.subtitle2,
            color = Color.White)
    }
}

@Composable
fun LoginButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .height(AppTheme.dimens.normalButtonHeight),
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = LightColors.surface),
        onClick = onClick
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.subtitle2,
            color = Color.White)
    }
}

@Composable
fun SmallClickableWithIconAndText(
    modifier: Modifier = Modifier,
    iconVector: ImageVector = Icons.Outlined.Add,
    iconContentDescription: String = "",
    text: String = "",
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.clickable {
            onClick.invoke()
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = iconVector,
            contentDescription = iconContentDescription,
            tint = MaterialTheme.colors.primary
        )
        Text(
            modifier = Modifier.padding(start = AppTheme.dimens.paddingSmall),
            text = text,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.primary
        )
    }
}